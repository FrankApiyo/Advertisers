(ns advertisers.views
  (:require [re-frame.core :as re-frame]
            [advertisers.subs :as subs]))

(defn table []
  (let [advertisers @(re-frame/subscribe
                      [::subs/advertisers])
        loading? @(re-frame/subscribe
                   [::subs/loading?])
        error-state?
        @(re-frame/subscribe
          [::subs/error-state?])]
    [:table {:class "bg-[#222] border-collapse border border-slate-500 w-11/12 m-5 text-justify border-collapse"}
     [:thead
      [:tr
       [:th {:class "border border-[#343434]-600 text-justify"} "Advertiser"]
       [:th {:class "border border-[#343434]-600 text-justify"} "Creation date"]
       [:th {:class "border border-[#343434]-600 text-justify"} "# Campaigns"]]]
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
         [:<>]
         (mapv (fn [{:keys [name createdAt campaignIds]}]
                 [:tr
                  [:td
                   {:class "border border-[#343434]-700"} name]
                  [:td
                   {:class "border border-[#343434]-700"} createdAt]
                  [:td
                   {:class "border border-[#343434]-700"} (count campaignIds)]])
               advertisers)))]]))

(defn overview []
  [:div {:class "flex min-w-full flex-col text-slate-300"}
   [:p "Overview of advertisers"]
   [:br]
   [table]])

