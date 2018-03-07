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

package com.consol.citrus.simulator.sample.file.filter;


import com.consol.citrus.simulator.exception.SimulatorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.stereotype.Component;

/**
 * While polling the input directory, only those files should be read for which a corresponding .done file is present.
 * For example, a file Hello.xml should be read only if Hello.done is present.
 */

@Component("DoneFilter")
public class DoneFilter implements FileListFilter<File>  {

    @Override
    public List<File> filterFiles(File[] files) {
        return
            Arrays.stream(files).filter(f -> f.getName().endsWith(".xml"))
                .filter(p -> {
                    File fileDone = new File(p.getAbsolutePath().replace(".xml", ".done"));
                    boolean result = fileDone.exists();
                    if (result) {
                        try {
                            Files.delete(fileDone.toPath());
                        } catch (IOException e) {
                            throw new SimulatorException("Could not delete done file: " + e.getMessage());
                        }
                    }
                    return result;
                })
                .collect(Collectors.toList());
    }
}
