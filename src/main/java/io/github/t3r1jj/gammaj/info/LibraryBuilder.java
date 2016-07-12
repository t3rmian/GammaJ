/* 
 * Copyright 2016 Damian Terlecki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.t3r1jj.gammaj.info;

public class LibraryBuilder {

    private String nameShort;
    private String nameLong;
    private String licenseShort;
    private String licenseLong;
    private String url;
    private String version;

    public LibraryBuilder() {
    }

    public LibraryBuilder setNameLong(String nameLong) {
        this.nameLong = nameLong;
        return this;
    }

    public LibraryBuilder setNameShort(String nameShort) {
        this.nameShort = nameShort;
        return this;
    }

    public LibraryBuilder setLicenseShort(String licenseShort) {
        this.licenseShort = licenseShort;
        return this;
    }

    public LibraryBuilder setLicenseLong(String licenseLong) {
        this.licenseLong = licenseLong;
        return this;
    }

    public LibraryBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public LibraryBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public Library createLibrary() {
        return new Library(nameLong, nameShort, licenseLong, licenseShort, url, version);
    }

}
