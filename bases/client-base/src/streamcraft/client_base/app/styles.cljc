(ns streamcraft.client-base.app.styles
  (:require [hyperfiddle.electric-dom2 :as dom]))

(defmacro hover-lift []
  `(dom/props {:class "transition-all transform hover:scale-110 hover:shadow-lg"}))
