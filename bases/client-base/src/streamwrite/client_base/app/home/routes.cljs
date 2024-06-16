(ns streamwrite.client-base.app.home.routes
  (:require [streamwrite.client-base.app.home.core :as core]
            [streamwrite.client-base.app.layout :as layout]))

(def routes
  ["/home" {:name   :home
            :layout layout/MainViewLayout
            :view   core/HomeView
            :title  "Home"}])
