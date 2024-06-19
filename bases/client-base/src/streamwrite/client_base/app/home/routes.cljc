(ns streamwrite.client-base.app.home.routes
  (:require [hyperfiddle.electric :as e]
            [streamwrite.client-base.app.home.views :as views]
            [streamwrite.client-base.app.layout :as layout]))

(e/def routes
  (e/client
    ["/home" {:name   :home
              :layout layout/MainViewLayout
              :view   views/HomeView
              :title  "Home"}]))
