(ns streamcraft.protocols.api.http-client
  (:refer-clojure :exclude [get]))

(defprotocol IHttpClient
  (request [this request]
    "Sends a ring request map and returns a ring response map."))
