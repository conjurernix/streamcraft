(ns streamwrite.client-base.app.home.views
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn HomeView []
  (e/client
    (dom/h1 (dom/text "Home View"))))
