(ns streamcraft.protocols.api.http-router)

(defprotocol IHttpRouterProvider
  (get-router [this])
  (get-routes [this]))
