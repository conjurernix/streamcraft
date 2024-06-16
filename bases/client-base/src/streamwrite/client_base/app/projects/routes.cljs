(ns streamwrite.client-base.app.projects.routes
  (:require [streamwrite.client-base.app.projects.core :as core]))

(def routes
  ["/projects" {:category :projects}
   ["" {:name  :projects
        :title "Projects"
        :view  core/ProjectsView}]
   ["/new" {:name  :project-new
            :title "New Project"
            :view  core/NewProjectView}]
   ["/:id" {:secondary-sidenav :projects
            :parameters        {:path [:map
                                       [:id :uuid]]}}
    ["" {:name :project
         :view core/ProjectView}]
    ["/documents" {:name  :project-documents
                   :title "Project Documents"
                   :view  core/ProjectDocumentsView}]
    ["/sources" {:name  :project-sources
                 :title "Project Sources"
                 :view  core/ProjectSourcesView}]]
   ["/documents"]])
