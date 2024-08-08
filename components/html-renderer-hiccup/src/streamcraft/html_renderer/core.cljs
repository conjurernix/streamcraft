(ns streamcraft.html-renderer.core
  (:require-macros [hiccups.core :refer [html]])
  (:require [hiccups.runtime]))

(defmacro render [body]
  `(html ~body))
