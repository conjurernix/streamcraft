(ns streamcraft.http-defaults.core
  (:require [reitit.openapi :as openapi]
            [reitit.ring :as ring]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.util.http-response :as http]))

(def default-middleware
  [;; swagger & openapi
   swagger/swagger-feature
   openapi/openapi-feature
   ;; query-params & form-params
   parameters/parameters-middleware
   ;; content-negotiation
   muuntaja/format-negotiate-middleware
   ;; encoding response body
   muuntaja/format-response-middleware
   ;; exception handling
   exception/exception-middleware
   ;; decoding request body
   muuntaja/format-request-middleware
   ;; multipart
   multipart/multipart-middleware])

(defn default-route-setup [routes]
  (->> routes
       (into [["/app/*" (ring/create-resource-handler
                          {:not-found-handler (fn [a b c]
                                                (http/not-found))})]
              ["/api"
               ["/ping" {:get {:handler   (constantly (http/ok "pong"))
                               :summary   "Endpoint to ping server for availability check"
                               :responses {200 {:body [:enum "pong"]}}}}]
               ["/docs/*"
                {:get (swagger-ui/create-swagger-ui-handler
                        {:url    "/api/swagger.json"
                         :config {:validator-url nil}})}]
               ["/swagger.json"
                {:get {:no-doc  true
                       :swagger {:info {:title       "API documentation"
                                        :description "swagger.json for API"}}
                       :handler (swagger/create-swagger-handler)}}]]])))