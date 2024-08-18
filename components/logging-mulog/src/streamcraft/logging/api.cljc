(ns streamcraft.logging.api
  (:require [streamcraft.logging.core :as core]))

(defmacro log! [message data]
  `(core/log! ~message ~data))

(defmacro info! [message data]
  `(core/info! ~message ~data))

(defmacro warn! [message data]
  `(core/warn! ~message ~data))

(defmacro error! [message data]
  `(core/error! ~message ~data))

(defmacro debug! [message data]
  `(core/debug! ~message ~data))

(defmacro with-context [ctx & body]
  `(core/with-context ~ctx ~@body))

(defmacro trace! [message opts & body]
  `(core/trace! ~message ~opts ~@body))

