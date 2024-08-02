(ns streamcraft.protocols.api.provider.http-handler)

(defprotocol IHttpHandlerProvider
  (get-handler [this]))
