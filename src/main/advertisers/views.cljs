(ns advertisers.views
  (:require [re-frame.core :as re-frame]
            [advertisers.events :as events]
            [advertisers.subs :as subs]))

(defn sorting-with-column
  "Conditionally return v or ^ depending on direction of sorting"
  [this-field]
  (let [reverse-sort @(re-frame/subscribe
                       [::subs/reverse-sort])
        sort-field @(re-frame/subscribe
                     [::subs/sort-field])]
    (when (= this-field sort-field)
      (if reverse-sort
        " ^"
        " v"))))

(defn table []
  (let [advertisers @(re-frame/subscribe
                      [::subs/enriched-advertisers])
        loading? @(re-frame/subscribe
                   [::subs/loading?])
        error-state?
        @(re-frame/subscribe
          [::subs/error-state?])]
    [:table {:class "bg-[#222] border-collapse border border-slate-500 w-11/12 m-5 mt-0.2 text-justify border-collapse"}
     [:thead {:class "cursor-pointer"}
      [:tr
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :name])}
        "ADVERTISER" (sorting-with-column :name)]
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :createdAt])}
        "CREATION DATA" (sorting-with-column :createdAt)]
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :campaign-id-count])}
        "# CAMPAIGNS" (sorting-with-column :campaign-id-count)]
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :impressions])}
        "IMPRESSIONS" (sorting-with-column :impressions)]
       [:th {:class "border border-[#343434]-600 text-justify"
             :on-click #(re-frame/dispatch
                         [::events/sort-advertisers :clicks])}
        "CLICKS" (sorting-with-column :clicks)]]]
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
                   {:class "border border-[#343434]-700"} (or impressions
                                                              "n/a")]
                  [:td
                   {:class "border border-[#343434]-700"} (or clicks
                                                              "n/a")]])
               advertisers)))]]))

(defn overview []
  [:div {:class "flex min-w-full flex-col text-slate-300 justify-center items-center"}
   [:div {:class "flex flex-col m-5 text-slate-300 w-11/12 border-slate-700 border-y-2 border-x-2 justify-center items-center bg-[#222]"}
    [:p {:class "w-11/12 m-5 p-4 border-b-slate-700 border-b-2"} "Overview of dvertisers"]
    [table]]])
