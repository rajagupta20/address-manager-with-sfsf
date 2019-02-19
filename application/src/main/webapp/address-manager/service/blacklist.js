"use strict";

sap.ui.define([
], function () {
    var api = {
        addBusinessPartnerToBlacklist: function (businessPartnerEmail) {
            return jQuery.get("/api/blacklistadd?email=" + businessPartnerEmail);
        },
        removeBusinessPartnerFromBlacklist: function (businessPartnerEmail) {
            return jQuery.get("/api/blacklistremove?email=" + businessPartnerEmail);
        },
        getBusinessPartnerBlacklistCount: function(businessPartnerEmail) {
            return jQuery.get("/api/blackliststatus?email=" + businessPartnerEmail);
        }
    };
    return api;
}, true);
