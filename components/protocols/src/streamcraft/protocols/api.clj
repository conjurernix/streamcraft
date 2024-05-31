(ns streamcraft.protocols.api)

(defprotocol IHttpRouterProvider
  (router [this])
  (routes [this]))

(defprotocol IHttpHandlerProvider
  (handler [this]))
