(ns streamcraft.config.core
  (:require [aero.core :as aero]
            #?(:clj [clojure.java.io :as io])))

(defn load-config! [config-path]
  #?(:clj     (-> config-path
                  (io/resource)
                  (aero/read-config {:profile (System/getenv "STREAMCRAFT_PROFILE")}))
     :default (-> config-path
                  (aero/read-config {:profile (System/getenv "STREAMCRAFT_PROFILE")}))))
