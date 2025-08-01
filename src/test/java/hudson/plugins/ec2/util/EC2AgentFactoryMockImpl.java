package hudson.plugins.ec2.util;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.plugins.ec2.AMITypeData;
import hudson.plugins.ec2.ConnectionStrategy;
import hudson.plugins.ec2.EC2AbstractSlave;
import hudson.plugins.ec2.EC2Computer;
import hudson.plugins.ec2.EC2OndemandSlave;
import hudson.plugins.ec2.EC2SpotSlave;
import hudson.plugins.ec2.EC2Tag;
import hudson.plugins.ec2.Tenancy;
import hudson.slaves.NodeProperty;
import java.io.IOException;
import java.io.Serial;
import java.util.List;
import software.amazon.awssdk.core.exception.SdkException;

@Extension(ordinal = 100)
public class EC2AgentFactoryMockImpl implements EC2AgentFactory {

    @Override
    public EC2OndemandSlave createOnDemandAgent(EC2AgentConfig.OnDemand config)
            throws Descriptor.FormException, IOException {
        return new MockEC2OndemandSlave(
                config.name,
                config.instanceId,
                config.description,
                config.remoteFS,
                config.numExecutors,
                config.labelString,
                config.mode,
                config.initScript,
                config.tmpDir,
                config.nodeProperties,
                config.remoteAdmin,
                config.javaPath,
                config.jvmopts,
                config.stopOnTerminate,
                config.idleTerminationMinutes,
                config.publicDNS,
                config.privateDNS,
                config.tags,
                config.cloudName,
                config.launchTimeout,
                config.amiType,
                config.connectionStrategy,
                config.maxTotalUses,
                config.tenancy);
    }

    @Override
    public EC2SpotSlave createSpotAgent(EC2AgentConfig.Spot config) throws Descriptor.FormException, IOException {
        return new MockEC2SpotSlave(
                config.name,
                config.spotInstanceRequestId,
                config.description,
                config.remoteFS,
                config.numExecutors,
                config.mode,
                config.initScript,
                config.tmpDir,
                config.labelString,
                config.nodeProperties,
                config.remoteAdmin,
                config.javaPath,
                config.jvmopts,
                config.idleTerminationMinutes,
                config.tags,
                config.cloudName,
                config.launchTimeout,
                config.amiType,
                config.connectionStrategy,
                config.maxTotalUses);
    }

    private static class MockEC2OndemandSlave extends EC2OndemandSlave {
        @Serial
        private static final long serialVersionUID = 1L;

        private MockEC2OndemandSlave(
                String name,
                String instanceId,
                String description,
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
                boolean useDedicatedTenancy,
                int launchTimeout,
                AMITypeData amiType,
                ConnectionStrategy connectionStrategy,
                int maxTotalUses)
                throws Descriptor.FormException, IOException {
            this(
                    name,
                    instanceId,
                    description,
                    remoteFS,
                    numExecutors,
                    labelString,
                    mode,
                    initScript,
                    tmpDir,
                    nodeProperties,
                    remoteAdmin,
                    javaPath,
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
                    Tenancy.Default);
        }

        private MockEC2OndemandSlave(
                String name,
                String instanceId,
                String description,
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
                Tenancy tenancy)
                throws Descriptor.FormException, IOException {
            super(
                    name,
                    instanceId,
                    description,
                    remoteFS,
                    numExecutors,
                    labelString,
                    mode,
                    initScript,
                    tmpDir,
                    nodeProperties,
                    remoteAdmin,
                    javaPath,
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
                    DEFAULT_METADATA_HOPS_LIMIT,
                    DEFAULT_METADATA_SUPPORTED,
                    DEFAULT_ENCLAVE_ENABLED);
        }

        @Override
        public Computer createComputer() {
            return new MockEC2Computer(this);
        }
    }

    private static class MockEC2SpotSlave extends EC2SpotSlave {
        @Serial
        private static final long serialVersionUID = 1L;

        private MockEC2SpotSlave(
                String name,
                String spotInstanceRequestId,
                String description,
                String remoteFS,
                int numExecutors,
                Mode mode,
                String initScript,
                String tmpDir,
                String labelString,
                List<? extends NodeProperty<?>> nodeProperties,
                String remoteAdmin,
                String javaPath,
                String jvmopts,
                String idleTerminationMinutes,
                List<EC2Tag> tags,
                String cloudName,
                int launchTimeout,
                AMITypeData amiType,
                ConnectionStrategy connectionStrategy,
                int maxTotalUses)
                throws Descriptor.FormException, IOException {
            super(
                    name,
                    spotInstanceRequestId,
                    description,
                    remoteFS,
                    numExecutors,
                    mode,
                    initScript,
                    tmpDir,
                    labelString,
                    nodeProperties,
                    remoteAdmin,
                    javaPath,
                    jvmopts,
                    idleTerminationMinutes,
                    tags,
                    cloudName,
                    launchTimeout,
                    amiType,
                    connectionStrategy,
                    maxTotalUses);
        }

        @Override
        public Computer createComputer() {
            return new MockEC2Computer(this);
        }
    }

    private static class MockEC2Computer extends EC2Computer {

        private MockEC2Computer(EC2AbstractSlave slave) {
            super(slave);
        }

        @Override
        public boolean isOffline() {
            return false;
        }

        @Override
        public long getUptime() throws SdkException {
            return 3500000;
        }
    }
}
