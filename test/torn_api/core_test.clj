(ns torn-api.core-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.spec.alpha :as s]
            [cheshire.core :as json]
            [torn-api.core :as c]
            [torn-api.profile :as profile]
            [torn-api.personal-stats :as pstats])
  (:import [java.time Instant]))

(deftest test-endpoint
  (is (s/valid? ::c/endpoint :user))
  (is (not (s/valid? ::c/endpoint :foo)))
  (is (not (s/valid? ::c/endpoint "user"))))

(deftest test-id
  (is (s/valid? ::c/id 1))
  (is (not (s/valid? ::c/id 0)))
  (is (not (s/valid? ::c/id "1"))))

(deftest test-selections
  (is (s/valid? ::c/selections [:foo :bar]))
  (is (not (s/valid? ::c/selections [])))
  (is (not (s/valid? ::c/selections ["foo" "bar"]))))

(deftest test-key
  (is (s/valid? ::c/key "foo"))
  (is (not (s/valid? ::c/key "")))
  (is (not (s/valid? ::c/key :foo))))

(deftest test-timestamp
  (is (s/valid? ::c/timestamp (Instant/EPOCH)))
  (is (not (s/valid? ::c/timestamp (java.util.Date.))))
  (is (not (s/valid? ::c/timestamp "2017-11-02T13:57:31.994-00:00"))))

(deftest test-request
  (let [valid-request {::c/endpoint :user
                       ::c/id 1
                       ::c/selections [:profile :battlestats]
                       ::c/key "apikey"
                       ::c/timestamp (Instant/EPOCH)}]
    (is (s/valid? ::c/request valid-request))
    (is (s/valid? ::c/request (dissoc valid-request ::c/id)))
    (is (s/valid? ::c/request (dissoc valid-request ::c/timestamp)))
    (is (not (s/valid? ::c/request (dissoc valid-request ::c/endpoint))))
    (is (not (s/valid? ::c/request (dissoc valid-request ::c/key))))
    (is (not (s/valid? ::c/request (dissoc valid-request ::c/selections))))))

(deftest test-url
  (let [request {::c/endpoint :user
                 ::c/id 1
                 ::c/selections [:profile :battlestats]
                 ::c/key "apikey"
                 ::c/timestamp (Instant/EPOCH)}]
    (is (= "https://api.torn.com/user/1" (c/url request)))
    (is (= "https://api.torn.com/user/" (c/url (dissoc request ::c/id))))))

(deftest test-query-params
  (let [request {::c/endpoint :user
                 ::c/id 1
                 ::c/selections [:profile :battlestats]
                 ::c/key "apikey"
                 ::c/timestamp (Instant/EPOCH)}]
    (is (= {:selections "profile,battlestats"
            :key "apikey"
            :timestamp 0}
           (c/query-params request)))
    (is (= {:selections "profile,battlestats"
            :key "apikey"
            :timestamp 100}
           (c/query-params (assoc request ::c/timestamp (Instant/ofEpochSecond 100)))))))

(defn const-client
  "Returns a torn-api.core/ApiClient which responds to every request with response"
  [response]
  (reify c/ApiClient
    (http-get [_ request _] (assoc response ::c/request request))))

(defn success-resp [body]
  {:status 200 :headers {"Content-Type" "application/json"} :body (json/encode body)})

(defn const-success-client
  "Returns a torn-api.core/ApiClient with a 200 response whose body is the passed in body, json-encoded."
  [body]
  (const-client (success-resp body)))

(deftest test-fetch
  (let [request {::c/endpoint :user
                 ::c/selections [:basic]
                 ::c/key "foo"}
        body {:level 1
              :gender "Female"
              :player_id 1
              :name "Player"
              :status ["Okay" ""]}
        client (const-success-client body)]
    (is (= body (-> (c/fetch request {} client) :body)))
    (is (= request (-> (c/fetch request {} client) ::c/request)))
    (is (thrown? clojure.lang.ExceptionInfo (c/fetch {} client))))
  (let [request #::c{:endpoint :user
                     :key "foo"
                     :selections [:profile :personalstats]}
        body {:role "Civilian"
              :faction {:position "Member"
                        :faction_id 16628
                        :days_in_faction 389
                        :faction_name "JFC"}
              :married {:spouse_id 1897243
                        :spouse_name "HandsomePants"
                        :duration 835}
              :age 836
              :property_id 260150
              :donator 1
              :property "Private Island"
              :name "sullengenie"
              :player_id 1946152
              :rank "Idolised Antagonist"
              :signup "2015-07-23 20:15:29"
              :karma 484
              :icons {:icon6 "Male"
                      :icon3 "Donator"
                      :icon4 "Subscriber"
                      :icon8 "Married - To HandsomePants"
                      :icon27 "Company - Employee of Just Fer Cruisin (Cruise Line)"
                      :icon9 "Faction - Member of JFC"
                      :icon35 "Bazaar - This person has items in their bazaar for sale"}
              :life {:current 4612
                     :maximum 4612
                     :increment 276
                     :interval 300
                     :ticktime 120
                     :fulltime 0}
              :level 64
              :friends 48
              :status ["Okay" ""]
              :last_action "2 minutes ago"
              :gender "Male"
              :enemies 9
              :awards 243
              :forum_posts 542
              :job {:position "Employee"
                    :company_id 48912
                    :company_name "Just Fer Cruisin"}
              :personalstats {:hospital 1815
                              :logins 2295}}
        client (const-success-client body)]
    (is (not (nil? (-> (c/fetch request {::c/coerce? true} client) :body ::profile/player-id))))
    (is (not (nil? (-> (c/fetch request {::c/coerce? true} client) :body ::pstats/logins))))
    (is (nil? (-> (c/fetch request {::c/coerce? true} client) :body :role)))))

(defn mock-client
  "Returns a torn-api.core/ApiClient which responds to a request with (handler request)"
  [handler]
  (reify c/ApiClient
    (http-get [_ request _]
      (assoc (handler request) ::c/request request))))

(deftest test-pfetch
  (let [req1 {::c/endpoint :user
              ::c/selections [:basic]
              ::c/key "foo1"}
        req2 {::c/endpoint :user
              ::c/selections [:basic]
              ::c/key "foo2"}
        resp1 {:level 1
               :gender "Female"
               :player_id 1
               :name "Player1"
               :status ["Okay" ""]}
        resp2 {:level 1
               :gender "Male"
               :player_id 2
               :name "Player2"
               :status ["Okay" ""]}
        client (mock-client (fn [req] (condp = (::c/key req)
                                        "foo1" (success-resp resp1)
                                        "foo2" (success-resp resp2))))]
    (is (= [resp1 resp2]
           (mapv :body (c/pfetch [req1 req2] {} client))))
    (is (contains? (first (c/pfetch [{}] {} client)) ::s/problems))
    (is (instance? Throwable (first (c/pfetch [req1] {} (mock-client (fn [req]
                                                                       (throw (Exception.))))))))))

(deftest test-coerce
  (let [response {::c/request #::c{:endpoint :user
                                   :key "foo"
                                   :selections [:profile :personalstats]}
                  :body {:role "Civilian"
                         :faction {:position "Member"
                                   :faction_id 16628
                                   :days_in_faction 389
                                   :faction_name "JFC"}
                         :married {:spouse_id 1897243
                                   :spouse_name "HandsomePants"
                                   :duration 835}
                         :age 836
                         :property_id 260150
                         :donator 1
                         :property "Private Island"
                         :name "sullengenie"
                         :player_id 1946152
                         :rank "Idolised Antagonist"
                         :signup "2015-07-23 20:15:29"
                         :karma 484
                         :icons {:icon6 "Male"
                                 :icon3 "Donator"
                                 :icon4 "Subscriber"
                                 :icon8 "Married - To HandsomePants"
                                 :icon27 "Company - Employee of Just Fer Cruisin (Cruise Line)"
                                 :icon9 "Faction - Member of JFC"
                                 :icon35 "Bazaar - This person has items in their bazaar for sale"}
                         :life {:current 4612
                                :maximum 4612
                                :increment 276
                                :interval 300
                                :ticktime 120
                                :fulltime 0}
                         :level 64
                         :friends 48
                         :status ["Okay" ""]
                         :last_action "2 minutes ago"
                         :gender "Male"
                         :enemies 9
                         :awards 243
                         :forum_posts 542
                         :job {:position "Employee"
                               :company_id 48912
                               :company_name "Just Fer Cruisin"}
                         :personalstats {:hospital 1815
                                         :logins 2295}}}
        coerced (c/coerce response)]
    (is (= (-> response :body :player_id) (-> coerced :body ::profile/player-id)))
    (is (= (-> response :body :life :maximum) (-> coerced :body ::profile/maximum-life)))
    (is (= (-> response :body :personalstats :hospital) (-> coerced :body ::pstats/times-hospitalized)))
    (is (thrown? Throwable (c/coerce (assoc-in response [::c/request ::c/selections] [:unsupported]))))))
