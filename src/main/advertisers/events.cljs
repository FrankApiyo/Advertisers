(ns advertisers.events
  (:require [advertisers.db :as db]
            [advertisers.subs :as subs]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [re-frame.core :as re-frame]))

(defn login
  []
  (swap! db/state assoc :auth? true))

(defn logout
  []
  (swap! db/state assoc :auth? false))

(defn toggle-user-dropdown
  []
  (let [dropdown (:user-dropdown? @db/state)]
    (swap! db/state assoc :user-dropdown? (not dropdown))))

(re-frame/reg-event-db
 ::success-fetch-advertisers
 (fn [db [_ response]]
   (assoc db
          :advertisers response
          :loading? false)))

(re-frame/reg-event-db
 ::failure-fetch-advertisers
 (fn [_db [_ response]]
   (js/console.log (clj->js response))))

(comment
  @(re-frame/subscribe [::subs/loading?]))

(re-frame/reg-event-fx
 ::fetch-advertisers
 (fn [{:keys [db]}]
   {:db   (assoc db :loading? true)
    :http-xhrio
    {:method          :get
     :uri             "https://5b87a97d35589600143c1424.mockapi.io/api/v1/advertisers"
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::success-fetch-advertisers]
     :on-failure      [::failure-fetch-advertisers]}}))