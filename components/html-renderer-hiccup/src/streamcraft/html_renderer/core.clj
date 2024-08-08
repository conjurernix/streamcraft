(ns streamcraft.html-renderer.core
  (:require [hiccup2.core :as h]))

(defmacro render [html]
  `(str (h/html ~html)))
