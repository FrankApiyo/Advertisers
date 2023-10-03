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

(defn get-url-search-params
  []
  (let [url (-> js/document .-location .-search)
        url-params (js/URLSearchParams. url)
        sort-name (.get url-params "sort-field")
        reverse? (.get url-params "reverse")]
    [sort-name (condp = reverse?
                 "false" false
                 "true" true
                 false)]))

(re-frame/reg-event-fx
 ::success-fetch-advertisers
 (fn [{:keys [db]} [_ response]]
   {:fx [[:dispatch [::fetch-ad-statistics]]]
    :db (assoc db
               :advertisers response
               :loading? false)}))

(re-frame/reg-event-db
 ::failure-fetch-advertisers
 (fn [db [_ _response]]
   (assoc db :error-state?
          true)))

(re-frame/reg-event-fx
 ::update-current-path
 (fn
   [_ [_ sort-name value overide?]]
   {:pre [(string? sort-name)
          (string? value)]}
   (let [current-url (js/URL. (.-URL js/document))
         search-params (doto (js/URLSearchParams.) (.set sort-name value))
         url (js/URL. (str (if overide?
                             (str
                              (.-origin current-url)
                              (.-pathname current-url))
                             (.-URL js/document))
                           (when (seq sort-name)
                             (str (if overide?
                                    "?"
                                    "&")
                                  (.toString search-params)))
                           (.-hash current-url)))]
     (-> js/window
         .-history
         (.replaceState {} "" url.href)))))

(re-frame/reg-event-fx
 ::sort-advertisers
 (fn [{:keys [db]} [_ sort-field reverse?]]
   (when sort-field
     (let [reverse? (or (:reverse-sort db) reverse?)]
       {:fx [[:dispatch [::update-current-path "sort-field" (name sort-field) true]]
             [:dispatch [::update-current-path "reverse" (str (or reverse? false))]]]
        :db (assoc db
                   :reverse-sort (not reverse?)
                   :enriched-advertisers
                   (sort-by sort-field
                            (if-not reverse?
                              <
                              >)
                            (:enriched-advertisers db))
                   :sort-field sort-field)}))))

(comment
  (sort ["Rowe, Cormier and Bruen" "Homenick - Von" "Emmerich and Sons" "Schaden, Jenkins and Swift" "Dach - Kerluke" "Williamson Inc" "Beier - Hills" "Hermann LLC" "Abbott - Lang" "Jacobson Inc" "Effertz - Rutherford" "Zulauf - Willms" "Kertzmann Inc" "Ortiz, Heller and Tremblay" "McClure - Harris"])
  (mapv
   :name
   (sort-by :name (vec @(re-frame/subscribe [::subs/advertisers]))))
  ;; (enrich-advertisers @(re-frame/subscribe [::subs/advertisers])
  ;;                     @(re-frame/subscribe [::subs/add-statistics]))
  (first (filter #(= "1" (:advertiserId %))
                 @(re-frame/subscribe [::subs/add-statistics]))))

(comment (let [reverse? ""]
           (condp = reverse?
             "false" false
             "true" true
             false)))

(defn enrich-advertisers
  [advertisers stats]
  (mapv
   (fn [{:keys [id campaignIds] :as advertiser}]
     (let [stats-for-id (first (filter #(= id (:advertiserId %))
                                       stats))]
       (assoc (merge stats-for-id advertiser)
              :campaign-id-count (count campaignIds))))
   advertisers))

(re-frame/reg-event-fx
 ::success-fetch-ad-statistics
 (fn [{:keys [db]} [_ response]]
   (let [[sort-name reverse?]
         (get-url-search-params)]
     {:fx [[:dispatch
            [::sort-advertisers (keyword sort-name) reverse?]]]
      :db (assoc db
                 :enriched-advertisers
                 (enrich-advertisers
                  (:advertisers db)
                  response)
                 :statistics response
                 :stats-loading? false)})))

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
