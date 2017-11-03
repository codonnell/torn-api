(ns torn-api.profile-test
  (:require [torn-api.profile :as profile]
            [clojure.test :refer [deftest is]])
  (:import [java.time Instant]))

(defn instants-within? [inst1 inst2 tol-ms]
  (< (Math/abs (- (.toEpochMilli inst1) (.toEpochMilli inst2)))
     tol-ms))

(deftest test-parse-last-action
  (is (instants-within? (. (Instant/now) minusSeconds 60)
                        (profile/parse-last-action "1 minute ago")
                        100))
  (is (instants-within? (. (Instant/now) minusSeconds (* 2 60))
                        (profile/parse-last-action "2 minutes ago")
                        100))
  (is (instants-within? (. (Instant/now) minusSeconds (* 60 60))
                        (profile/parse-last-action "1 hour ago")
                        100))
  (is (instants-within? (. (Instant/now) minusSeconds (* 5 60 60))
                        (profile/parse-last-action "5 hours ago")
                        100))
  (is (instants-within? (. (Instant/now) minusSeconds (* 60 60 24))
                        (profile/parse-last-action "1 day ago")
                        100))
  (is (instants-within? (. (Instant/now) minusSeconds (* 100 60 60 24))
                        (profile/parse-last-action "100 days ago")
                        100)))

(deftest test-coerce
  (let [uncoerced {:role "Civilian"
                   :faction {:position "Member"
                             :faction_id 16628
                             :days_in_faction 385
                             :faction_name "JFC"}
                   :married {:spouse_id 1897243
                             :spouse_name "HandsomePants"
                             :duration 832}
                   :age 833
                   :property_id 260150
                   :donator 1
                   :property "Private Island"
                   :name "sullengenie"
                   :player_id 1946152
                   :rank "Supreme Antagonist"
                   :signup "2015-07-23 20:15:29"
                   :karma 484
                   :icons {:icon6 "Male"}
                   :life {:current 4612
                          :maximum 4612
                          :increment 276
                          :interval 300
                          :ticktime 188
                          :fulltime 0}
                   :level 64
                   :friends 48
                   :status ["Okay" ""]
                   :last_action "4 minutes ago"
                   :gender "Male"
                   :enemies 9
                   :awards 242
                   :forum_posts 542
                   :job {:position "Employee"
                         :company_id 48912
                         :company_name "Just Fer Cruisin"}}
        coerced #:torn-api.profile{:role "Civilian"
                                   :maximum-life 4612
                                   :spouse-id 1897243
                                   :donator true
                                   :property "Private Island"
                                   :name "sullengenie"
                                   :days-in-faction 385
                                   :rank "Supreme Antagonist"
                                   :signup (Instant/parse "2015-07-23T20:15:29Z")
                                   :status ["Okay" ""]
                                   :player-id 1946152
                                   :karma 484
                                   :faction-id 16628
                                   :days-married 832
                                   :level 64
                                   :friends 48
                                   :forum-posts 542
                                   :current-life 4612
                                   :gender "Male"
                                   :enemies 9
                                   :faction-position "Member"
                                   :awards 242}]
    (is (= coerced (-> uncoerced profile/coerce (dissoc ::profile/last-action))))
    (is (instants-within? (. (Instant/now) minusSeconds (* 4 60))
                          (-> uncoerced profile/coerce ::profile/last-action)
                          100))
    (is (= false (-> uncoerced (assoc :donator 0) profile/coerce ::profile/donator)))
    (is (not (-> uncoerced
                 (assoc-in [:faction :faction_id] 0)
                 profile/coerce
                 (contains? ::profile/days-in-faction))))
    (is (not (-> uncoerced
                 (assoc-in [:married :spouse_id] 0)
                 profile/coerce
                 (contains? ::profile/days-married))))))
