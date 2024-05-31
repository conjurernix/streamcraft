(try
  (require 'hashp.core)
  (catch Throwable _))
(ns user
  (:require [streamcraft.bootstrap-server.core :refer [bootstrap-server!]]
            [streamcraft.config.api :as config]))

(def resources (-> (Thread/currentThread)
                   (.getContextClassLoader)
                   (.getResources "")
                   (enumeration-seq)))

#_(bootstrap-server! "admin-base/config.edn")