/*
 * Copyright 2006-2017 the original author or authors.
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

package com.consol.citrus.simulator.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This sample simulator demonstrates a primitive bank service that calculates and validates IBANs. It demonstrates how
 * to configure a simulator that uses:
 * <ul>
 * <li>HTTPS</li>
 * <li>REST</li>
 * <li>JSON</li>
 * <li>Request mapping based on HTTP query parameters</li>
 * </ul>
 */
@SpringBootApplication
public class BankServiceSimulator {

    public static void main(String[] args) {
        SpringApplication.run(BankServiceSimulator.class, args);
    }
}
