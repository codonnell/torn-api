(ns torn-api.profile
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.spec.alpha :as spec])
  (:import [java.time Instant LocalDateTime ZoneOffset]
           [java.time.temporal ChronoUnit]
           [java.time.format DateTimeFormatter]))

(def nonempty-string? (s/and string? #(not (string/blank? %))))
(defn timestamp? [t] (instance? Instant t))

(s/def ::role #{"Admin" "Officer" "Secretary" "Moderator" "Helper" "NPC" "Civilian"
                "Reporter" "Wiki Contributor" "Wiki Editor"})
(s/def ::rank nonempty-string?)
(s/def ::faction-id pos-int?)
(s/def ::days-in-faction integer?)
(s/def ::faction-position #{"None" "Member" "Leader" "Co-Leader"})
(s/def ::spouse-id pos-int?)
(s/def ::days-married integer?)
(s/def ::player-id pos-int?)
(s/def ::donator boolean?)
(s/def ::property nonempty-string?)
(s/def ::name nonempty-string?)
(s/def ::signup timestamp?)
(s/def ::status (s/coll-of string?))
(s/def ::karma integer?)
(s/def ::current-life integer?)
(s/def ::maximum-life pos-int?)
(s/def ::level pos-int?)
(s/def ::friends integer?)
(s/def ::last-action timestamp?)
(s/def ::gender #{"Male" "Female"})
(s/def ::enemies integer?)
(s/def ::awards integer?)
(s/def ::forum-posts integer?)

(s/def ::profile
  (s/keys :req [::role ::rank ::player-id ::donator ::property ::name
                ::signup ::karma ::current-life ::maximum-life ::level
                ::friends ::last-action ::gender ::enemies ::awards
                ::forum-posts]
          :opt [::faction-id ::days-in-faction ::faction-position
                ::spouse-id ::days-married]))

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
   ::days-in-faction {:path [:faction :days_in_faction]}
   ::faction-position {:path [:faction :position]}
   ::spouse-id {:path [:married :spouse_id] :coercer zero->nil}
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
   ::forum-posts {:path [:forum_posts]}})

(defn remove-marriage-length-from-unmarried [m]
  (if-not (contains? m ::spouse-id)
    (dissoc m ::days-married)
    m))

(defn remove-faction-info-from-unfactioned [m]
  (if-not (contains? m ::faction-id)
    (dissoc m ::faction-position ::days-in-faction)
    m))

(defn coerce
  "Coerces a profile API response so that it satisfies the :torn-api.profile/profile spec."
  [body]
  (-> (reduce-kv (fn [m k {:keys [path coercer] :or {coercer identity}}]
                   (let [v (-> body (get-in path) coercer)]
                     (if (nil? v) m (assoc m k v))))
                 {}
                 key-coercions)
      remove-marriage-length-from-unmarried
      remove-faction-info-from-unfactioned))

(defn valid?
  "Checks whether an already-coerced response matches the :torn-api.profile/profile spec."
  [profile]
  (spec/valid? ::profile profile))
