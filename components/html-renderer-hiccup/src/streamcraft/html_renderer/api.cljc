(ns streamcraft.html-renderer.api
  (:require [streamcraft.html-renderer.core :as core]))

(defmacro render [html]
  (core/render html))
