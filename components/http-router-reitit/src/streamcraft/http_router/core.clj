(ns streamcraft.http-router.core
  (:require [com.stuartsierra.component :as component]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as rcm]
            [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.util.http-response :as http]
            [streamcraft.protocols.api.http-router :as router]
            [taoensso.timbre :as log]))

(defn- -make-reitit-router [routes middleware electric-handler config]
  (let [{:keys [resources-path]} config]
    (ring/router
      (->> routes
           (into [["/app"
                   ["*" {:get {:handler electric-handler}}]]
                  ["/js/*" (ring/create-resource-handler
                             {:root              (str resources-path "/js")
                              :not-found-handler (constantly http/not-found)})]
                  ["/css/*" (ring/create-resource-handler
                              {:root              (str resources-path "/css")
                               :not-found-handler (constantly http/not-found)})]
                  ["/public/*" (ring/create-resource-handler
                                 {:root              resources-path
                                  :not-found-handler (constantly http/not-found)})]
                  ["/api"
                   routes
                   ["/ping" {:get {:handler   (constantly (http/ok "pong"))
                                   :summary   "Endpoint to ping server"
                                   :responses {200 {:body [:enum "pong"]}}}}]
                   ["/docs/*"
                    {:get (swagger-ui/create-swagger-ui-handler
                            {:url    "/api/swagger.json"
                             :config {:validator-url nil}})}]
                   ["/swagger.json"
                    {:get {:no-doc  true
                           :swagger {:info {:title       "API documentation"
                                            :description "swagger.json for API"}}
                           :handler (swagger/create-swagger-handler)}}]]]))
      {:conflicts (constantly nil)
       :data      {:muuntaja   m/instance
                   :coercion   rcm/coercion
                   :middleware middleware}})))

(defrecord ReititRouter
  [config middleware electric-handler entrypoint routes router]
  component/Lifecycle

  (start [this]
    (if router
      this
      (do (log/info "Starting ReititRouter")
          (assoc this :router (-make-reitit-router routes middleware electric-handler config)))))
  (stop [this]
    (if router
      (do (log/info "Stopping ReititRouter")
          (-> this
              (assoc :config nil)
              (assoc :middleware nil)
              (assoc :entrypoint nil)
              (assoc :electric-handler nil)
              (assoc :routes nil)
              (assoc :router nil)))
      this))


  router/IHttpRouterProvider
  (get-routes [_this] routes)
  (get-router [_this] router))

(defn make-router []
  (map->ReititRouter {}))