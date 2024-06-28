(ns streamcraft.frontend-router.api
  (:refer-clojure :exclude [name])
  (:require [hyperfiddle.electric :as e]
            #?@(:cljs [[missionary.core :as ms]
                       [reitit.frontend :as rf]
                       [reitit.frontend.easy :as rfe]
                       [reitit.coercion.malli :as rcm]])))

(e/def router)

(e/def match)

(e/def data)

(e/def name)

#_(defmacro make-router [routes]
    `(->> (ms/observe
            (fn [!]
              (rfe/start!
                (rf/router routes {:data {:coercion rcm/coercion}})
                !
                {:use-fragment false})))
          (ms/relieve {})
          new))