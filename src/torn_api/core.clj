(ns torn-api.core
  (:require [clojure.spec.alpha :as spec]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as string])
  (:import [java.time Instant]))

(spec/def ::endpoint #{:user :properties :faction :company :item-market :torn})
(spec/def ::selections (spec/and (spec/coll-of keyword?) not-empty))
(spec/def ::id pos-int?)
(spec/def ::key (spec/and string? #(not (string/blank? %))))
(spec/def ::timestamp #(instance? Instant %))

(spec/def ::request (spec/keys :req [::endpoint ::selections ::key]
                               :opt [::id ::timestamp]))

(defprotocol ApiClient
  (http-get [_ request opts] "Returns a ring response map with the request included at :torn-api.core/request"))

(defn url [{:keys [::endpoint ::id ::selections ::key ::timestamp]}]
  (format "https://api.torn.com/%s/%s" (name endpoint) (or id "")))

(defn query-params [request]
  {:selections (mapv name (::selections request))
   :key (::key request)
   :timestamp (when-let [t (::timestamp request)] (.getEpochSecond t))})

(def api-client
  (reify
    ApiClient
    (http-get [_ request opts]
      (let [response (http/get (url request) (assoc opts :query-params (query-params request)))]
        (assoc response ::request request)))))

(defn process-response [response]
  (update response :body #(json/decode % true)))

(defn fetch
  "Executes the request, passing opts to client. request must conform to the
  torn-api.core/request spec. Throws if request is invalid. client must
  implement the torn-api.core/ApiClient protocol. By default, uses
  torn-api.core/api-client, a thin wrapper around clj-http.client. Consult the
  clj-http documentation for available opts."
  ([request]
   (fetch request {}))
  ([request opts]
   (fetch request opts api-client))
  ([request opts client]
   (if (spec/valid? ::request request)
     (-> (http-get client request opts)
         process-response)
     (throw (ex-info "Invalid request" (spec/explain-data ::request request))))))

(defn pfetch
  "Executes the requests with a connection pool, passing opts to client. The
  keys :torn-api.core/timeout, :torn-api.core/threads, :torn-api.core/insecure?,
  and :torn-api.core/default-per-route will be passed along to the connection
  manager. Returns a vector containing the response objects. If a request is
  invalid, returns a map with error data in place of the response. If an
  exception is otherwise thrown while getting hte response, the exception will
  be return in place of the response. client must implement the
  torn-api.core/ApiClient protocol. By default, uses torn-api.core/api-client, a
  thin wrapper around clj-http.client. Consult the clj-http documentation for
  available opts."
  ([requests]
   (pfetch requests {}))
  ([requests opts]
   (pfetch requests opts api-client))
  ([requests
    {:keys [::timeout ::threads ::insecure? ::default-per-route] :as opts}
    client]
   (http/with-connection-pool {:timeout timeout :threads threads :insecure? insecure?
                               :default-per-route default-per-route}
     (mapv (fn [request]
             (try
               (fetch request opts client)
               (catch clojure.lang.ExceptionInfo e
                 (ex-data e))
               (catch Throwable e
                 e)))
           requests))))
