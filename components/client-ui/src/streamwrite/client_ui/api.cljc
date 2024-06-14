(ns streamwrite.client-ui.api
  (:require [conjurernix.electric-franken.api :as ui]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

#?(:clj (defonce !projects (atom [{:name        "Intelligent Traffic Management System"
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
                                   :tags        ["ai" "ml" "nlp" "customer-support"]}])))

(e/def projects (e/server (e/watch !projects)))

(defmacro hover-lift []
  `(dom/props {:class "transition-all transform hover:scale-110 hover:shadow-lg"}))

(e/defn ProjectCard [{:keys [name description tags] :as _project}]
  (e/client
    (dom/a (dom/props {:href "#"})
      (ui/Card
        (dom/props {:class "p-4"})
        (hover-lift)
        (ui/CardTitle (dom/text name))
        (ui/CardBody (dom/text description))
        (when (seq tags)
          (ui/CardFooter
            (e/for [tag tags]
              (ui/Label
                (ui/label-primary)
                (dom/props {:class "mr-2"})
                (hover-lift)
                (dom/text tag)))))))))

(e/defn ListProjectView []
  (e/client
    (dom/div (dom/props {:class   "uk-child-width-1-3 pt-8"
                         :uk-grid ""})
      (e/for [project projects]
        (dom/div
          (ProjectCard. project))))))

(e/defn MainViewLayout [{:keys [title view breadcrumbs]}]
  (e/client
    (dom/div (dom/props {:class "p-8"})
      (dom/div (dom/props {:class "flex flex-col gap-2"})
        (dom/div (dom/props {:class "flex flex-col gap-1"})
          (dom/h1 (dom/text title))
          (ui/SmallDivider (dom/props {:class "h-4"}))
          (ui/Breadcrumb
            (e/for [breadcrumb-item breadcrumbs]
              (ui/BreadcrumbItem
                (dom/a
                  (dom/props {:href "#"})
                  (dom/text breadcrumb-item))))
            (ui/BreadcrumbItem
              (dom/text title))))
        (ui/IconDivider)
        (view.)))))

(e/defn Main [ring-request]
  (e/client
    (binding [dom/node js/document.body]
      (dom/div (dom/props {:class "w-screen h-screen flex flex-col"})
        (dom/div (dom/props {:class "flex justify-between items-center border-b border-gray-500 px-24 py-3"})
          (ui/Logo
            (dom/props {:href "/"})
            (dom/text "Logo"))
          (ui/Inline
            (dom/props {:class "mr-2"})
            (ui/Icon "user")
            (ui/Dropdown
              (ui/DropdownNav
                (ui/NavItem
                  (dom/a (dom/props {:href "#"})
                    (dom/text "Profile")))
                (ui/NavItem
                  (dom/a (dom/props {:href "#"})
                    (dom/text "Settings")))))))
        (dom/div (dom/props {:class "flex h-full"})
          (dom/div (dom/props {:class "w-full sm:w-64 border-r border-gray-500 p-4"})
            (ui/Nav
              (ui/nav-primary)
              (ui/NavItem
                (dom/a (dom/props {:href "#"})
                  (ui/Icon "home"
                    (dom/props {:class "mr-2"}))
                  (dom/text "Home")))
              (ui/NavItem
                (ui/parent)
                (dom/a (dom/props {:href "#"})
                  (ui/Icon "file-text"
                    (dom/props {:class "mr-2"}))
                  (dom/text "Projects")
                  (ui/NavParentIcon))
                (ui/NavSub
                  (ui/NavItem
                    (dom/a (dom/props {:href "#"})
                      (ui/Icon "plus"
                        (dom/props {:class "mr-2"}))
                      (dom/text "New Project")))
                  (ui/NavItem
                    (dom/a (dom/props {:href "#"})
                      (ui/Icon "list"
                        (dom/props {:class "mr-2"}))
                      (dom/text "List Projects")))))
              (ui/NavItem
                (ui/parent)
                (dom/a (dom/props {:href "#"})
                  (ui/Icon "database"
                    (dom/props {:class "mr-2"}))
                  (dom/text "Integrations")
                  (ui/NavParentIcon))
                (ui/NavSub
                  (ui/NavItem
                    (dom/a (dom/props {:href "#"})
                      (ui/Icon "plus"
                        (dom/props {:class "mr-2"}))
                      (dom/text "New Integration")))
                  (ui/NavItem
                    (dom/a (dom/props {:href "#"})
                      (ui/Icon "list"
                        (dom/props {:class "mr-2"}))
                      (dom/text "List Integrations")))))))
          (dom/div (dom/props {:class "flex-grow w-full"})
            (MainViewLayout. {:title       "List Projects"
                              :view        ListProjectView
                              :breadcrumbs ["Home" "Projects"]})))))))
