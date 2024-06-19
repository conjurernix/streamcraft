(ns streamwrite.client-base.app.project.views
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [conjurernix.electric-franken.api :as ui]
            [streamwrite.client-base.app.styles :as styles]
            [streamwrite.frontend-router.api :as frontend-router]
            #?@(:cljs [[reitit.frontend.easy :as rfe]])))

#?(:clj (defonce !projects (atom (->> [{:name        "Intelligent Traffic Management System"
                                        :description "An intelligent traffic-management system uses AI and machine learning
                                    to control traffic signals, depending on road traffic. The project could involve
                                     using sensors or real-time video feeds to track current traffic conditions and
                                     then employ machine learning algorithms to optimize signal timings accordingly."
                                        :tags        ["ai" "ml" "iot"]}
                                       {:name        "Personalized Medicine and Predictive Algorithms in Healthcare"
                                        :description "Personalized medicine is an emerging trend in healthcare focused on
                                   tailoring medical decisions and practices to individual patients."
                                        :tags        ["health" "ai" "ml"]}
                                       {:name        "Autonomous Drone Navigation"
                                        :description "This project involves creating an autonomous navigation system for drones using image recognition and machine learning algorithms. The drone should be able to identify and avoid obstacles in real-time, and navigate to specific locations autonomously."
                                        :tags        ["ai" "ml" "drones" "image-recognition"]}
                                       {:name        "Predictive Policing Using Machine Learning"
                                        :description "This project aims to use machine learning and data analysis methods to predict areas of high crime activity, to help law enforcement agencies strategically allocate their resources."
                                        :tags        ["ai" "ml" "law-enforcement" "data-analysis"]}
                                       {:name        "Real-Time Fraud Detection in Banking"
                                        :description "In this project, the goal is to develop a real-time fraud detection system for banking transactions. This system would use machine learning algorithms to identify and flag potentially fraudulent transactions based on several factors."
                                        :tags        ["ai" "ml" "banking" "fraud-detection"]}
                                       {:name        "Natural Language Processing for Customer Support"
                                        :description "This project involves creating a Natural Language Processing (NLP) system that can understand and respond to customer queries, greatly reducing the time needed for customer support staff to handle common questions."
                                        :tags        ["ai" "ml" "nlp" "customer-support"]}]
                                      (map #(assoc % :id (random-uuid)))))))

(e/def projects (e/server (e/watch !projects)))

(e/defn ProjectSecondarySideNav []
  (e/client
    (let [active frontend-router/name
          {:keys [parameters]} frontend-router/match
          {:keys [path]} parameters]
      (ui/Nav
        (ui/nav-primary)
        (ui/NavItem
          (when (= active :project) (ui/active))
          (dom/a (dom/props {:href (rfe/href :project path)})
            (ui/Icon "git-branch"
              (dom/props {:class "mr-2"}))
            (dom/text "Stream")))
        (ui/NavItem
          (when (= active :project-documents) (ui/active))
          (dom/a (dom/props {:href (rfe/href :project-documents path)})
            (ui/Icon "file-text"
              (dom/props {:class "mr-2"}))
            (dom/text "Documents")))
        (ui/NavItem
          (when (= active :project-sources) (ui/active))
          (dom/a (dom/props {:href (rfe/href :project-sources path)})
            (ui/Icon "pull"
              (dom/props {:class "mr-2"}))
            (dom/text "Sources")))))))

(e/defn ProjectCard [{:keys [id name description tags] :as _project}]
  (e/client
    (dom/a (dom/props {:href (rfe/href :project {:id id})})
      (ui/Card
        (dom/props {:class "p-4"})
        (styles/hover-lift)
        (ui/CardTitle (dom/text name))
        (ui/CardBody
          (dom/p
            (dom/props {:class "line-clamp-3"})
            (dom/text description)))
        (when (seq tags)
          (ui/CardFooter
            (e/for [tag tags]
              (ui/Label
                (ui/label-primary)
                (dom/props {:class "mr-2"})
                (styles/hover-lift)
                (dom/text tag)))))))))

(e/defn ProjectsView []
  (e/client
    (dom/div (dom/props {:class "flex flex-col gap-2"})
      (dom/div (dom/props {:class "flex justify-end"})
        (ui/ButtonLink (dom/props {:href (rfe/href :project-new)})
          (ui/button-primary)
          (dom/text "New ")
          (ui/Icon "plus"
            (dom/props {:class "ml-2"}))))
      (dom/div (dom/props {:class   "uk-child-width-1-4 pt-8"
                           :uk-grid ""})
        (e/for [project projects]
          (dom/div
            (ProjectCard. project)))))))

(e/defn NewProjectView []
  (e/client
    (dom/h1 (dom/text "Create new Project"))))

(e/defn ProjectView []
  (e/client
    (dom/h1 (dom/text "Project"))))

(e/defn EditProjectView []
  (e/client
    (dom/h1 (dom/text "Hello World"))))

(e/defn ProjectDocumentsView []
  (e/client
    (dom/h1 (dom/text "Hello World"))))

(e/defn ProjectSourcesView []
  (e/client
    (dom/h1 (dom/text "Hello World"))))