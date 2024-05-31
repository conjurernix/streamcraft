(try
  (require 'hashp.core)
  (catch Throwable _))
(ns user
  (:require [streamcraft.bootstrap-server.core :refer [bootstrap-server!]]
            [streamcraft.config.api :as config]))

(def config (config/load-config! "client-base/config.edn"))

(bootstrap-server! "client-base/config.edn")