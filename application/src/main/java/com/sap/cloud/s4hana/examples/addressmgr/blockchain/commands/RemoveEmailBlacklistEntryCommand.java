package com.sap.cloud.s4hana.examples.addressmgr.blockchain.commands;

import com.google.common.base.Optional;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sap.cloud.s4hana.examples.addressmgr.blockchain.BlacklistService;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.frameworks.hystrix.Command;
import org.slf4j.Logger;

public class RemoveEmailBlacklistEntryCommand extends Command<Optional<Boolean>> {
    private static final Logger logger = CloudLoggerFactory.getLogger(RemoveEmailBlacklistEntryCommand.class);

    private final BlacklistService blacklistService;

    private final String email;

    public RemoveEmailBlacklistEntryCommand( BlacklistService blacklistService, final String email ) {
        super(HystrixCommandGroupKey.Factory.asKey("LeonardoHyperledgerFabric-blacklistremove"), 5000);
        this.blacklistService = blacklistService;
        this.email = email;
    }

    @Override
    protected Optional<Boolean> run() throws Exception {
        blacklistService.removeEmailFromBlacklist(getEmail());
        return Optional.of(Boolean.TRUE);
    }

    public String getEmail() {
        return email;
    }

    @Override
    protected Optional<Boolean> getFallback() {
        logger.error("Fallback called when removing email {} from blacklist. Reason:",
                getEmail(), getExecutionException());
        return Optional.absent();
    }

    @Override
    public String toString() {
        return "RemoveEmailBlacklistStatusCommand{" +
                "email='" + email + '\'' +
                '}';
    }
}
