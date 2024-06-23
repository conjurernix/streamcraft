(ns streamcraft.protocols.api.http-handler)

(defprotocol IHttpHandlerProvider
  (get-handler [this]))
