(ns streamwrite.client-base.app.layout
  (:require [conjurernix.electric-franken.api :as ui]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [streamwrite.frontend-router.api :as frontend-router]
            [streamwrite.client-base.app.project.views :as projects]
            #?@(:cljs [[reitit.frontend :as rf]
                       [reitit.frontend.easy :as rfe]])))

(e/defn AppBar []
  (e/client
    (dom/div (dom/props {:class "flex justify-between items-center border-b border-gray-500 px-24 py-3"})
      (ui/Logo
        (dom/props {:href (rfe/href :home)})
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
                (dom/text "Settings")))))))))

(e/defn MainSideNav []
  (e/client
    (let [active frontend-router/name
          {:keys [category]} frontend-router/data]
      (ui/Nav
        (ui/nav-primary)
        (ui/NavItem
          (when (= active :home) (ui/active))
          (dom/a (dom/props {:href (rfe/href :home)})
            (ui/Icon "home"
              (dom/props {:class "mr-2"}))
            (dom/text "Home")))
        (ui/NavItem
          (ui/parent)
          (when (= category :projects) (ui/active))
          (dom/a (dom/props {:href "#"})
            (ui/Icon "folder"
              (dom/props {:class "mr-2"}))
            (dom/text "Projects")
            (ui/NavParentIcon))
          (ui/NavSub
            (ui/NavItem
              (when (= active :projects) (ui/active))
              (dom/a (dom/props {:href (rfe/href :projects)})
                (ui/Icon "list"
                  (dom/props {:class "mr-2"}))
                (dom/text "Projects")))
            (ui/NavItem
              (when (= active :project-new) (ui/active))
              (dom/a (dom/props {:href (rfe/href :project-new)})
                (ui/Icon "plus"
                  (dom/props {:class "mr-2"}))
                (dom/text "New Project")))))
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
                (ui/Icon "list"
                  (dom/props {:class "mr-2"}))
                (dom/text "Integrations")))
            (ui/NavItem
              (dom/a (dom/props {:href "#"})
                (ui/Icon "plus"
                  (dom/props {:class "mr-2"}))
                (dom/text "New Integration")))))))))

(e/defn MainPageLayout [view]
  (e/client
    (let [{:keys [secondary-sidenav]} frontend-router/data]
      (dom/div (dom/props {:class "w-screen h-screen flex flex-col"})
        (AppBar.)
        (dom/div (dom/props {:class "flex h-full"})
          (dom/div (dom/props {:class "flex flex-col gap-4 justify-around w-1/6 border-r border-gray-500 p-4 h-full"})
            (dom/div (dom/props {:class "flex-1"})
              (MainSideNav.))
            (ui/IconDivider (dom/props {:class "h-4"}))
            (dom/div (dom/props {:class "flex-1"})
              (case secondary-sidenav
                :projects (projects/ProjectSecondarySideNav.)
                (dom/div))))
          (dom/div (dom/props {:class "flex-1"})
            (view.)))))))

(e/defn resolve-title [title]
  (e/client
    (if (string? title)
      title
      (title. frontend-router/match))))

(e/defn MainViewLayout [{:keys [title view breadcrumbs]}]
  (e/client
    (dom/div (dom/props {:class "p-8"})
      (dom/div (dom/props {:class "flex flex-col gap-2"})
        (dom/div (dom/props {:class "flex flex-col gap-1"})
          (dom/h1 (dom/text title))
          (ui/SmallDivider (dom/props {:class "h-4"}))
          (when (seq breadcrumbs)
            (ui/Breadcrumb
              (e/for [breadcrumb-item breadcrumbs]
                (let [{:keys [name title]}
                      (some-> frontend-router/router
                              (rf/match-by-name breadcrumb-item)
                              :data)]
                  (ui/BreadcrumbItem
                    (dom/a
                      (dom/props {:href (rfe/href name)})
                      (dom/text (resolve-title. title))))))
              (ui/BreadcrumbItem
                (dom/text title)))))
        (ui/IconDivider)
        (view.)))))

(e/defn NotFoundView []
  (e/client
    (dom/h1 (dom/text "Not Found"))))