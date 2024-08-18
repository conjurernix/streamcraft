(ns streamcraft.logging.core
  (:require [com.brunobonacci.mulog :as mu]))

(defmacro log! [event data]
  `(mu/log ~event ~@(flatten (seq data))))

(defmacro info! [event data]
  `(log! ~event (assoc ~data :level :info)))

(defmacro warn! [event data]
  `(log! ~event (assoc ~data :level :warn)))

(defmacro error! [event data]
  `(log! ~event (assoc ~data :level :error)))

(defmacro debug! [event data]
  `(log! ~event (assoc ~data :level :debug)))

(defmacro with-context [ctx & body]
  `(mu/with-context ~ctx
     ~@body))

(defmacro trace! [event opts & body]
  `(let [opts# ~opts
         trace-opts# (if (map? opts#) opts# {})]
     (mu/trace ~event
       trace-opts#
       ~@body)))
