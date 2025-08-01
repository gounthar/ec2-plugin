package hudson.plugins.ec2;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Descriptor.FormException;
import hudson.model.Node;
import hudson.plugins.ec2.ssh.EC2MacLauncher;
import hudson.plugins.ec2.ssh.EC2UnixLauncher;
import hudson.plugins.ec2.ssh.EC2WindowsSSHLauncher;
import hudson.plugins.ec2.win.EC2WindowsLauncher;
import hudson.slaves.NodeProperty;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest2;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;

/**
 * Agent running on EC2.
 *
 * @author Kohsuke Kawaguchi
 */
public class EC2OndemandSlave extends EC2AbstractSlave {
    private static final Logger LOGGER = Logger.getLogger(EC2OndemandSlave.class.getName());

    @Deprecated
    public EC2OndemandSlave(
            String instanceId,
            String templateDescription,
            String remoteFS,
            int numExecutors,
            String labelString,
            Mode mode,
            String initScript,
            String tmpDir,
            String remoteAdmin,
            String jvmopts,
            boolean stopOnTerminate,
            String idleTerminationMinutes,
            String publicDNS,
            String privateDNS,
            List<EC2Tag> tags,
            String cloudName,
            int launchTimeout,
            AMITypeData amiType)
            throws FormException, IOException {
        this(
                instanceId,
                templateDescription,
                remoteFS,
                numExecutors,
                labelString,
                mode,
                initScript,
                tmpDir,
                remoteAdmin,
                jvmopts,
                stopOnTerminate,
                idleTerminationMinutes,
                publicDNS,
                privateDNS,
                tags,
                cloudName,
                false,
                launchTimeout,
                amiType);
    }

    @Deprecated
    public EC2OndemandSlave(
            String instanceId,
            String templateDescription,
            String remoteFS,
            int numExecutors,
            String labelString,
            Mode mode,
            String initScript,
            String tmpDir,
            String remoteAdmin,
            String jvmopts,
            boolean stopOnTerminate,
            String idleTerminationMinutes,
            String publicDNS,
            String privateDNS,
            List<EC2Tag> tags,
            String cloudName,
            boolean useDedicatedTenancy,
            int launchTimeout,
            AMITypeData amiType)
            throws FormException, IOException {
        this(
                instanceId,
                templateDescription,
                remoteFS,
                numExecutors,
                labelString,
                mode,
                initScript,
                tmpDir,
                remoteAdmin,
                jvmopts,
                stopOnTerminate,
                idleTerminationMinutes,
                publicDNS,
                privateDNS,
                tags,
                cloudName,
                false,
                useDedicatedTenancy,
                launchTimeout,
                amiType);
    }

    @Deprecated
    public EC2OndemandSlave(
            String instanceId,
            String templateDescription,
            String remoteFS,
            int numExecutors,
            String labelString,
            Mode mode,
            String initScript,
            String tmpDir,
            String remoteAdmin,
            String jvmopts,
            boolean stopOnTerminate,
            String idleTerminationMinutes,
            String publicDNS,
            String privateDNS,
            List<EC2Tag> tags,
            String cloudName,
            boolean usePrivateDnsName,
            boolean useDedicatedTenancy,
            int launchTimeout,
            AMITypeData amiType)
            throws FormException, IOException {
        this(
                templateDescription + " (" + instanceId + ")",
                instanceId,
                templateDescription,
                remoteFS,
                numExecutors,
                labelString,
                mode,
                initScript,
                tmpDir,
                Collections.emptyList(),
                remoteAdmin,
                jvmopts,
                stopOnTerminate,
                idleTerminationMinutes,
                publicDNS,
                privateDNS,
                tags,
                cloudName,
                useDedicatedTenancy,
                launchTimeout,
                amiType,
                ConnectionStrategy.backwardsCompatible(usePrivateDnsName, false, false),
                -1);
    }

    @Deprecated
    public EC2OndemandSlave(
            String name,
            String instanceId,
            String templateDescription,
            String remoteFS,
            int numExecutors,
            String labelString,
            Mode mode,
            String initScript,
            String tmpDir,
            List<? extends NodeProperty<?>> nodeProperties,
            String remoteAdmin,
            String jvmopts,
            boolean stopOnTerminate,
            String idleTerminationMinutes,
            String publicDNS,
            String privateDNS,
            List<EC2Tag> tags,
            String cloudName,
            boolean useDedicatedTenancy,
            int launchTimeout,
            AMITypeData amiType,
            ConnectionStrategy connectionStrategy,
            int maxTotalUses)
            throws FormException, IOException {
        this(
                name,
                instanceId,
                templateDescription,
                remoteFS,
                numExecutors,
                labelString,
                mode,
                initScript,
                tmpDir,
                nodeProperties,
                remoteAdmin,
                jvmopts,
                stopOnTerminate,
                idleTerminationMinutes,
                publicDNS,
                privateDNS,
                tags,
                cloudName,
                launchTimeout,
                amiType,
                connectionStrategy,
                maxTotalUses,
                Tenancy.backwardsCompatible(useDedicatedTenancy));
    }

    @Deprecated
    public EC2OndemandSlave(
            String name,
            String instanceId,
            String templateDescription,
            String remoteFS,
            int numExecutors,
            String labelString,
            Mode mode,
            String initScript,
            String tmpDir,
            List<? extends NodeProperty<?>> nodeProperties,
            String remoteAdmin,
            String jvmopts,
            boolean stopOnTerminate,
            String idleTerminationMinutes,
            String publicDNS,
            String privateDNS,
            List<EC2Tag> tags,
            String cloudName,
            int launchTimeout,
            AMITypeData amiType,
            ConnectionStrategy connectionStrategy,
            int maxTotalUses,
            Tenancy tenancy)
            throws FormException, IOException {
        this(
                name,
                instanceId,
                templateDescription,
                remoteFS,
                numExecutors,
                labelString,
                mode,
                initScript,
                tmpDir,
                nodeProperties,
                remoteAdmin,
                DEFAULT_JAVA_PATH,
                jvmopts,
                stopOnTerminate,
                idleTerminationMinutes,
                publicDNS,
                privateDNS,
                tags,
                cloudName,
                launchTimeout,
                amiType,
                connectionStrategy,
                maxTotalUses,
                tenancy,
                DEFAULT_METADATA_ENDPOINT_ENABLED,
                DEFAULT_METADATA_TOKENS_REQUIRED,
                DEFAULT_METADATA_HOPS_LIMIT);
    }

    @Deprecated
    public EC2OndemandSlave(
            String name,
            String instanceId,
            String templateDescription,
            String remoteFS,
            int numExecutors,
            String labelString,
            Mode mode,
            String initScript,
            String tmpDir,
            List<? extends NodeProperty<?>> nodeProperties,
            String remoteAdmin,
            String javaPath,
            String jvmopts,
            boolean stopOnTerminate,
            String idleTerminationMinutes,
            String publicDNS,
            String privateDNS,
            List<EC2Tag> tags,
            String cloudName,
            int launchTimeout,
            AMITypeData amiType,
            ConnectionStrategy connectionStrategy,
            int maxTotalUses,
            Tenancy tenancy,
            Boolean metadataEndpointEnabled,
            Boolean metadataTokensRequired,
            Integer metadataHopsLimit)
            throws FormException, IOException {
        this(
                name,
                instanceId,
                templateDescription,
                remoteFS,
                numExecutors,
                labelString,
                mode,
                initScript,
                tmpDir,
                nodeProperties,
                remoteAdmin,
                DEFAULT_JAVA_PATH,
                jvmopts,
                stopOnTerminate,
                idleTerminationMinutes,
                publicDNS,
                privateDNS,
                tags,
                cloudName,
                launchTimeout,
                amiType,
                connectionStrategy,
                maxTotalUses,
                tenancy,
                metadataEndpointEnabled,
                metadataTokensRequired,
                metadataHopsLimit,
                DEFAULT_METADATA_SUPPORTED);
    }

    @Deprecated
    public EC2OndemandSlave(
            String name,
            String instanceId,
            String templateDescription,
            String remoteFS,
            int numExecutors,
            String labelString,
            Mode mode,
            String initScript,
            String tmpDir,
            List<? extends NodeProperty<?>> nodeProperties,
            String remoteAdmin,
            String javaPath,
            String jvmopts,
            boolean stopOnTerminate,
            String idleTerminationMinutes,
            String publicDNS,
            String privateDNS,
            List<EC2Tag> tags,
            String cloudName,
            int launchTimeout,
            AMITypeData amiType,
            ConnectionStrategy connectionStrategy,
            int maxTotalUses,
            Tenancy tenancy,
            Boolean metadataEndpointEnabled,
            Boolean metadataTokensRequired,
            Integer metadataHopsLimit,
            Boolean metadataSupported)
            throws FormException, IOException {
        this(
                name,
                instanceId,
                templateDescription,
                remoteFS,
                numExecutors,
                labelString,
                mode,
                initScript,
                tmpDir,
                nodeProperties,
                remoteAdmin,
                DEFAULT_JAVA_PATH,
                jvmopts,
                stopOnTerminate,
                idleTerminationMinutes,
                publicDNS,
                privateDNS,
                tags,
                cloudName,
                launchTimeout,
                amiType,
                connectionStrategy,
                maxTotalUses,
                tenancy,
                metadataEndpointEnabled,
                metadataTokensRequired,
                metadataHopsLimit,
                metadataSupported,
                DEFAULT_ENCLAVE_ENABLED);
    }

    @DataBoundConstructor
    public EC2OndemandSlave(
            String name,
            String instanceId,
            String templateDescription,
            String remoteFS,
            int numExecutors,
            String labelString,
            Mode mode,
            String initScript,
            String tmpDir,
            List<? extends NodeProperty<?>> nodeProperties,
            String remoteAdmin,
            String javaPath,
            String jvmopts,
            boolean stopOnTerminate,
            String idleTerminationMinutes,
            String publicDNS,
            String privateDNS,
            List<EC2Tag> tags,
            String cloudName,
            int launchTimeout,
            AMITypeData amiType,
            ConnectionStrategy connectionStrategy,
            int maxTotalUses,
            Tenancy tenancy,
            Boolean metadataEndpointEnabled,
            Boolean metadataTokensRequired,
            Integer metadataHopsLimit,
            Boolean metadataSupported,
            Boolean enclaveEnabled)
            throws FormException, IOException {
        super(
                name,
                instanceId,
                templateDescription,
                remoteFS,
                numExecutors,
                mode,
                labelString,
                (amiType.isWinRMAgent()
                        ? new EC2WindowsLauncher()
                        : (amiType.isWindows()
                                ? new EC2WindowsSSHLauncher()
                                : (amiType.isMac() ? new EC2MacLauncher() : new EC2UnixLauncher()))),
                new EC2RetentionStrategy(idleTerminationMinutes),
                initScript,
                tmpDir,
                nodeProperties,
                remoteAdmin,
                javaPath,
                jvmopts,
                stopOnTerminate,
                idleTerminationMinutes,
                tags,
                cloudName,
                launchTimeout,
                amiType,
                connectionStrategy,
                maxTotalUses,
                tenancy,
                metadataEndpointEnabled,
                metadataTokensRequired,
                metadataHopsLimit,
                metadataSupported,
                enclaveEnabled);
        this.publicDNS = publicDNS;
        this.privateDNS = privateDNS;
    }

    /**
     * Constructor for debugging.
     */
    public EC2OndemandSlave(String instanceId) throws FormException, IOException {
        this(
                instanceId,
                instanceId,
                "debug",
                "/tmp/hudson",
                1,
                "debug",
                Mode.NORMAL,
                "",
                "/tmp",
                Collections.emptyList(),
                null,
                null,
                false,
                null,
                "Fake public",
                "Fake private",
                null,
                null,
                false,
                0,
                new UnixData(null, null, null, null, null),
                ConnectionStrategy.PRIVATE_IP,
                -1);
    }

    /**
     * Terminates the instance in EC2.
     */
    @Override
    public void terminate() {
        if (terminateScheduled.getCount() == 0) {
            synchronized (terminateScheduled) {
                if (terminateScheduled.getCount() == 0) {
                    Computer.threadPoolForRemoting.submit(() -> {
                        try {
                            if (!isAlive(true)) {
                                /*
                                 * The node has been killed externally, so we've nothing to do here
                                 */
                                LOGGER.info("EC2 instance already terminated: " + getInstanceId());
                            } else {
                                Ec2Client ec2 = getCloud().connect();
                                TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                                        .instanceIds(Collections.singletonList(getInstanceId()))
                                        .build();
                                ec2.terminateInstances(request);
                                LOGGER.info("Terminated EC2 instance (terminated): " + getInstanceId());
                            }
                            Jenkins.get().removeNode(this);
                            LOGGER.info("Removed EC2 instance from jenkins controller: " + getInstanceId());
                        } catch (SdkException | IOException e) {
                            LOGGER.log(Level.WARNING, "Failed to terminate EC2 instance: " + getInstanceId(), e);
                        } finally {
                            synchronized (terminateScheduled) {
                                terminateScheduled.countDown();
                            }
                        }
                    });
                    terminateScheduled.reset();
                }
            }
        }
    }

    @Override
    public Node reconfigure(final StaplerRequest2 req, JSONObject form) throws FormException {
        if (form == null) {
            return null;
        }

        if (!isAlive(true)) {
            LOGGER.info("EC2 instance terminated externally: " + getInstanceId());
            try {
                Jenkins.get().removeNode(this);
            } catch (IOException ioe) {
                LOGGER.log(
                        Level.WARNING,
                        "Attempt to reconfigure EC2 instance which has been externally terminated: " + getInstanceId(),
                        ioe);
            }

            return null;
        }

        return super.reconfigure(req, form);
    }

    @Extension
    public static final class DescriptorImpl extends EC2AbstractSlave.DescriptorImpl {
        @Override
        public String getDisplayName() {
            return Messages.EC2OndemandSlave_AmazonEC2();
        }
    }

    @Override
    public String getEc2Type() {
        return Messages.EC2OndemandSlave_OnDemand();
    }
}
