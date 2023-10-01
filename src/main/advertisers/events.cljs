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
 (fn [db [_ _response]]
   (assoc db :error-state?
          true)))

(re-frame/reg-event-db
 ::sort-advertisers
 (fn [db [_ sort-field]]
   (assoc db
          :reverse-sort (not (:reverse-sort db))
          :advertisers
          (sort sort-field (:advertisers db))
          :sort-field sort-field)))

(comment
  @(re-frame/subscribe [::subs/loading?]))

(re-frame/reg-event-db
 ::success-fetch-ad-statistics
 (fn [db [_ response]]
   ;; use this data to enrich advertisers data
   (assoc db
          :add-statistics response
          :stats-loading? false)))

(re-frame/reg-event-db
 ::failure-fetch-ad-statistics
 (fn [db [_ _response]]
   (assoc db :stats-error-state?
          true)))


(re-frame/reg-event-fx
 ::fetch-ad-statistics
 (fn [{:keys [db]}]
   {:db   (assoc db
                 :stats-loading? true
                 :stats-error-state? false)
    :http-xhrio
    {:method          :get
     :uri             "https://5b87a97d35589600143c1424.mockapi.io/api/v1/advertiser-statistics"
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::success-fetch-ad-statistics]
     :on-failure      [::failure-fetch-ad-statistics]}}))

(re-frame/reg-event-fx
 ::fetch-advertisers
 (fn [{:keys [db]}]
   {:db   (assoc db
                 :loading? true
                 :error-state? false)
    :http-xhrio
    {:method          :get
     :uri             "https://5b87a97d35589600143c1424.mockapi.io/api/v1/advertisers"
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::success-fetch-advertisers]
     :on-failure      [::failure-fetch-advertisers]}}))