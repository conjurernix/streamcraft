(ns streamcraft.http-middleware.core
  (:require [reitit.openapi :as openapi]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]))

(def middleware
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
   multipart/multipart-middleware
   rrc/coerce-exceptions-middleware
   rrc/coerce-request-middleware
   rrc/coerce-response-middleware])
