(ns advertisers.app
  (:require [reagent.dom :as dom]
            [advertisers.views :as views]
            [re-frame.core :as re-frame]
            [advertisers.events :as events]))

(defn app
  []
  [views/overview])

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (dom/render [app]
    (.getElementById js/document "app")))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (re-frame/dispatch-sync [::events/fetch-advertisers])
  (re-frame/dispatch-sync [::events/fetch-ad-statistics])
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
