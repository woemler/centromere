/*
 * Copyright 2019 the original author or authors
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

package com.blueprint.centromere.core.etl.writer;

import com.blueprint.centromere.core.exceptions.DataProcessingException;
import java.io.File;

/**
 * File write object, specifically for writing temporary files to the file system.
 *
 * @author woemler
 */
public interface TempFileWriter {

    /**
     * Generates a temporary file to be written by the writer component.  Allows
     *   the component and external objects to reference a temporary file.
     *
     * @param inputFile input file object
     * @return temporary file object
     */
    default File getTempFile(File inputFile) throws DataProcessingException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        return new File(tempDir, inputFile.getAbsolutePath() + ".tmp");
    }

}
