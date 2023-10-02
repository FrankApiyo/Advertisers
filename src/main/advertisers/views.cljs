(ns advertisers.views
  (:require [re-frame.core :as re-frame]
            [advertisers.events :as events]
            [advertisers.subs :as subs]))

;; TODO: sorting state is saved in URL

(defn table []
  (let [advertisers @(re-frame/subscribe
                      [::subs/enriched-advertisers])
        loading? @(re-frame/subscribe
                   [::subs/loading?])
        error-state?
        @(re-frame/subscribe
          [::subs/error-state?])]
    [:table {:class "bg-[#222] border-collapse border border-slate-500 w-11/12 m-5 text-justify border-collapse"}
     [:thead {:class "cursor-pointer"}
      [:tr
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :name])}
        "ADVERTISERS"]
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :createdAt])}
        "CREATION DATA"]
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :campaignIds])}
        "# CAMPAIGNS"]
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :impressions])}
        "IMPRESSIONS"]
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :clicks])}
        "CLICKS"]]]
     [:tbody
      (cond
        error-state?
        [:tr
         [:td {:class "border border-[#343434]-700"} "Could not load data :("]]
        (or loading? (nil? loading?))
        [:tr
         [:td {:class "border border-[#343434]-700"} "Loading..."]]
        :else
        (cons
         [:<> {:key "random-key"}]
         (mapv (fn [{:keys [name createdAt campaignIds impressions clicks id]}]
                 [:tr
                  {:key id}
                  [:td
                   {:class "border border-[#343434]-700"} name]
                  [:td
                   {:class "border border-[#343434]-700"} createdAt]
                  [:td
                   {:class "border border-[#343434]-700"} (count campaignIds)]
                  [:td
                   {:class "border border-[#343434]-700"} impressions]
                  [:td
                   {:class "border border-[#343434]-700"} clicks]])
               advertisers)))]]))

(defn overview []
  [:div {:class "flex min-w-full flex-col text-slate-300"}
   [:p "Overview of advertisers"]
   [:br]
   [table]])

