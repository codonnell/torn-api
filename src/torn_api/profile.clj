(ns torn-api.profile
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string])
  (:import [java.time Instant LocalDateTime ZoneOffset]
           [java.time.temporal ChronoUnit]
           [java.time.format DateTimeFormatter]))

(def nonempty-string? (s/and string? #(not (string/blank? %))))
(defn timestamp? [t] (instance? Instant t))

(s/def ::role #{"Admin" "Officer" "Secretary" "Moderator" "Helper" "NPC" "Civilian"
                "Reporter" "Wiki Contributor" "Wiki Editor"})
(s/def ::rank nonempty-string?)
(s/def ::faction-id pos-int?)
(s/def ::faction-name nonempty-string?)
(s/def ::days-in-faction nat-int?)
(s/def ::faction-position #{"None" "Member" "Leader" "Co-Leader"})
(s/def ::spouse-id pos-int?)
(s/def ::spouse-name nonempty-string?)
(s/def ::days-married nat-int?)
(s/def ::player-id pos-int?)
(s/def ::donator boolean?)
(s/def ::property nonempty-string?)
(s/def ::name nonempty-string?)
(s/def ::signup timestamp?)
(s/def ::status (s/coll-of string?))
(s/def ::karma nat-int?)
(s/def ::current-life nat-int?)
(s/def ::maximum-life pos-int?)
(s/def ::level pos-int?)
(s/def ::friends nat-int?)
(s/def ::last-action timestamp?)
(s/def ::gender #{"Male" "Female"})
(s/def ::enemies nat-int?)
(s/def ::awards nat-int?)
(s/def ::forum-posts nat-int?)
(s/def ::company-id pos-int?)
(s/def ::company-position nonempty-string?)
(s/def ::company-name nonempty-string?)

(s/def ::profile
  (s/keys :req [::role ::rank ::player-id ::donator ::property ::name
                ::signup ::karma ::current-life ::maximum-life ::level
                ::friends ::last-action ::gender ::enemies ::awards
                ::forum-posts]
          :opt [::faction-id ::faction-name ::days-in-faction ::faction-position
                ::spouse-id ::spouse-name ::days-married]))

(defn zero->nil [n]
  (if (= 0 n) nil n))

(def ^:private time-unit
  {"minute" ChronoUnit/MINUTES
   "hour" ChronoUnit/HOURS
   "day" ChronoUnit/DAYS})

(defn parse-last-action [s]
  (let [[_ n unit] (re-find #"(\d+) (minute|hour|day)s? ago" s)]
    (.minus (Instant/now) (Long/parseLong n) (time-unit unit))))

(defn parse-signup [s]
  (-> s
      (LocalDateTime/parse (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))
      (.toInstant ZoneOffset/UTC)))

(def key-coercions
  "A map with a key for each profile datum and whose value is a map with :path, a
  vector to be passed to get-in that finds the datum in the torn api response,
  and an optional :coercer, a function of one argument that will be applied to
  the datum value."
  {::role {:path [:role]}
   ::rank {:path [:rank]}
   ::faction-id {:path [:faction :faction_id] :coercer zero->nil}
   ::faction-name {:path [:faction :faction_name]}
   ::days-in-faction {:path [:faction :days_in_faction]}
   ::faction-position {:path [:faction :position]}
   ::spouse-id {:path [:married :spouse_id] :coercer zero->nil}
   ::spouse-name {:path [:married :spouse_name]}
   ::days-married {:path [:married :duration]}
   ::player-id {:path [:player_id]}
   ::donator {:path [:donator] :coercer #(if (= 1 %) true false)}
   ::property {:path [:property]}
   ::name {:path [:name]}
   ::signup {:path [:signup] :coercer parse-signup}
   ::status {:path [:status]}
   ::karma {:path [:karma]}
   ::current-life {:path [:life :current]}
   ::maximum-life {:path [:life :maximum]}
   ::level {:path [:level]}
   ::friends {:path [:friends]}
   ::last-action {:path [:last_action] :coercer parse-last-action}
   ::gender {:path [:gender]}
   ::enemies {:path [:enemies]}
   ::awards {:path [:awards]}
   ::forum-posts {:path [:forum_posts]}
   ::company-id {:path [:job :company_id] :coercer zero->nil}
   ::company-name {:path [:job :company_name]}
   ::company-position {:path [:job :position]}})

(def dependent-keys
  {::faction-id [::faction-id ::faction-position ::faction-name ::days-in-faction]
   ::spouse-id [::spouse-id ::days-married ::spouse-name]
   ::company-id [::company-id ::company-name ::company-position]})

(defn remove-marriage-length-from-unmarried [m]
  (if-not (contains? m ::spouse-id)
    (dissoc m ::days-married ::spouse-name)
    m))

(defn remove-faction-info-from-unfactioned [m]
  (if-not (contains? m ::faction-id)
    (dissoc m ::faction-position ::days-in-faction ::faction-name)
    m))

(defn remove-dependent-keys [response]
  (reduce-kv (fn [m k dep-ks]
               (if-not (contains? response k)
                 (apply dissoc m dep-ks)
                 m))
             response
             dependent-keys))

(defn coerce
  "Coerces a profile API response so that it satisfies the :torn-api.profile/profile spec."
  [body]
  (-> (reduce-kv (fn [m k {:keys [path coercer] :or {coercer identity}}]
                   (let [v (-> body (get-in path) coercer)]
                     (if (nil? v) m (assoc m k v))))
                 {}
                 key-coercions)
      remove-dependent-keys))

(defn valid?
  "Checks whether an already-coerced response matches the :torn-api.profile/profile spec."
  [profile]
  (s/valid? ::profile profile))
