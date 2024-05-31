(ns streamcraft.config.core
  (:require [aero.core :as aero]
            #?(:clj [clojure.java.io :as io])))

(defn load-config! [config-path]
  #?(:clj     (-> config-path
                  (io/resource)
                  (aero/read-config))
     :default (-> config-path
                  (aero/read-config))))
