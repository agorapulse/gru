/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.spring.heist

import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import com.agorapulse.gru.minions.AbstractMinion
import com.agorapulse.gru.spring.Spring

class NastyMinion extends AbstractMinion<Spring> {

    final int index = INTERCEPTORS_MINION_INDEX

    NastyMinion() {
        super(Spring)
    }

    @Override
    protected GruContext doAfterRun(Spring client, Squad squad, GruContext context) {
        return context.withResult('You haven\'t expected this, have you?')
    }

}
