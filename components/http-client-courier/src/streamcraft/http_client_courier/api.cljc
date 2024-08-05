(ns streamcraft.http-client-courier.api
  (:require [streamcraft.http-client-courier.core :as core]))

(defn make-http-client []
  (core/make-http-client))