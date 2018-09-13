/*
 * Copyright 2018 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.streamsets.pipeline.stage.destination.pulsar;

import com.streamsets.pipeline.api.Config;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.StageUpgrader;
import com.streamsets.pipeline.api.impl.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PulsarTargetUpgrader implements StageUpgrader {
  private static final Logger LOG = LoggerFactory.getLogger(PulsarTargetUpgrader.class);

  @Override
  public List<Config> upgrade(
      String library,
      String stageName,
      String stageInstance,
      int fromVersion,
      int toVersion,
      List<Config> configs
  ) throws StageException {
    switch (fromVersion) {
      case 1:
        upgradeV1ToV2(configs);
        if (toVersion == 2) {
          break;
        }
        // fall through
      default:
        throw new IllegalStateException(Utils.format("Unexpected fromVersion {}", fromVersion));
    }

    return configs;
  }

  private void upgradeV1ToV2(List<Config> configs) {
    configs.add(new Config("pulsarConfig.maxPendingMessages", 1000));
    configs.add(new Config("pulsarConfig.asyncSend", true));
    configs.add(new Config("pulsarConfig.enableBatching", true));
    configs.add(new Config("pulsarConfig.batchMaxMessages", 2000));
    configs.add(new Config("pulsarConfig.batchMaxPublishDelay", 1000));
  }
}
