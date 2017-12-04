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

package com.consol.citrus.simulator.sample.model;

import java.util.Arrays;
import java.util.Optional;

public enum Bank {
    UNKNOWN("Unknown", "Unknown", "Unknown"),
    ABC("12345670", "ABCDEFG5670", "The Wealthy ABC bank"),
    DEF("12345671", "ABCDEFG5671", "The Poor DEF bank");

    private String sortCode;
    private String bic;
    private String longName;

    Bank(String sortCode, String bic, String longName) {
        this.sortCode = sortCode;
        this.bic = bic;
        this.longName = longName;
    }

    public String sortCode() {
        return sortCode;
    }

    public String bic() {
        return bic;
    }

    public String longName() {
        return longName;
    }

    public static Optional<Bank> bySortCode(final String sortCode) {
        return Arrays.stream(Bank.values())
                .filter(bank -> sortCode.equalsIgnoreCase(bank.sortCode))
                .findFirst();
    }
}
