/*
 * Copyright the original author or authors.
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

package org.citrusframework.simulator.sample.model;

import org.citrusframework.simulator.sample.util.JsonMarshaller;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CalculateIbanResponse {
    private BankAccount bankAccount;

    public String asJson() {
        return JsonMarshaller.toJson(this);
    }

    public static CalculateIbanResponse fromJson(String json) {
        return JsonMarshaller.fromJson(json, CalculateIbanResponse.class);
    }
}
