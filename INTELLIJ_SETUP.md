# IntelliJ IDEA Setup & Troubleshooting Guide

## 🔧 Initial Setup Steps

### 1. Enable Lombok Plugin

1. Open IntelliJ IDEA
2. Go to **File** → **Settings** (or **IntelliJ IDEA** → **Preferences** on Mac)
3. Navigate to **Plugins**
4. Search for "**Lombok**"
5. Click **Install** (if not already installed)
6. Click **Apply** and **Restart IDE**

### 2. Enable Annotation Processing

1. Go to **File** → **Settings** (or **IntelliJ IDEA** → **Preferences** on Mac)
2. Navigate to **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
3. Check "**Enable annotation processing**"
4. Set "**Obtain processors from project classpath**"
5. Click **Apply** and **OK**

### 3. Configure Java SDK

1. Go to **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. Under **Project Settings** → **Project**:
   - **SDK**: Select Java 17 (or download if not available)
   - **Language level**: 17 - Sealed types, always-strict floating-point semantics
3. Under **Project Settings** → **Modules**:
   - Ensure **Language level** is set to **17**
4. Click **Apply** and **OK**

### 4. Configure Maven Settings

1. Go to **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Maven**
2. Set:
   - **Maven home directory**: (use bundled or specify path)
   - **User settings file**: (default is fine)
   - **Local repository**: (default is fine)
3. Under **Maven** → **Runner**:
   - **JRE**: Select Java 17
   - **VM Options**: Add `-Xmx2048m` (optional, for more memory)
4. Click **Apply** and **OK**

## 🐛 Troubleshooting Common Errors

### Error: `java.lang.ExceptionInInitializerError` with Lombok

**Cause**: Lombok annotation processor not properly configured with IntelliJ's compiler

**Solutions**:

#### Solution 1: Update pom.xml (Already Done ✅)
The updated `pom.xml` now includes proper Lombok configuration in the maven-compiler-plugin.

#### Solution 2: Invalidate Caches
1. Go to **File** → **Invalidate Caches**
2. Check all options:
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
3. Click **Invalidate and Restart**

#### Solution 3: Reimport Maven Project
1. Open the **Maven** tool window (View → Tool Windows → Maven)
2. Click the **Reload All Maven Projects** button (circular arrow icon)
3. Wait for the import to complete

#### Solution 4: Delete IntelliJ Project Files
1. Close IntelliJ IDEA
2. Delete these files/folders from your project directory:
   ```bash
   rm -rf .idea/
   rm *.iml
   ```
3. Reopen the project in IntelliJ
4. Let IntelliJ reimport the Maven project

#### Solution 5: Clean and Rebuild
1. Go to **Build** → **Clean Project**
2. After cleaning completes, go to **Build** → **Rebuild Project**

#### Solution 6: Update Lombok Plugin
1. Go to **File** → **Settings** → **Plugins**
2. Find "Lombok" plugin
3. Click **Update** if available
4. Restart IntelliJ

#### Solution 7: Check Java Compiler Settings
1. Go to **File** → **Settings** → **Build, Execution, Deployment** → **Compiler** → **Java Compiler**
2. Set:
   - **Project bytecode version**: 17
   - **Per-module bytecode version**: 17 for all modules
3. Uncheck "**Use '--release' option for cross-compilation (Java 9 and later)**" (try this if issue persists)
4. Click **Apply** and **OK**

### Error: Cannot find symbol (Lombok generated methods)

**Solutions**:
1. Ensure Lombok plugin is installed and enabled
2. Enable annotation processing (see step 2 in Initial Setup)
3. Rebuild project: **Build** → **Rebuild Project**

### Error: Maven build works but IntelliJ shows errors

**Solutions**:
1. Go to **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Maven** → **Importing**
2. Ensure "**Import Maven projects automatically**" is checked
3. Check "**Automatically download: Sources, Documentation**"
4. Reimport Maven project

### Error: Wrong Java version

**Solutions**:
1. Verify Java 17 is installed:
   ```bash
   java -version
   ```
2. In IntelliJ, go to **File** → **Project Structure** → **SDKs**
3. Ensure Java 17 is listed
4. If not, click **+** → **Add JDK** and locate Java 17 installation

## 📋 Complete Clean Setup Procedure

If all else fails, follow these steps for a complete clean setup:

### Step 1: Close IntelliJ

### Step 2: Clean Project Directory
```bash
cd order-saga-demo
mvn clean
rm -rf .idea/
rm *.iml
rm -rf target/
```

### Step 3: Clean Maven Cache (Optional)
```bash
rm -rf ~/.m2/repository/org/projectlombok/
rm -rf ~/.m2/repository/io/temporal/
```

### Step 4: Verify pom.xml
Ensure the updated `pom.xml` is in place with:
- Lombok version: 1.18.30
- Maven compiler plugin with annotation processor configuration

### Step 5: Command Line Build Test
```bash
mvn clean install
```

If this succeeds, the issue is IntelliJ-specific.

### Step 6: Open in IntelliJ
1. Open IntelliJ IDEA
2. **File** → **Open** → Select `order-saga-demo` folder
3. Choose "**Open as Project**"
4. Wait for Maven import to complete (bottom right status bar)

### Step 7: Configure IntelliJ
Follow all steps in "Initial Setup Steps" section above

### Step 8: Build in IntelliJ
1. **Build** → **Rebuild Project**
2. Check for any errors in the **Build** tool window

## 🎯 Quick Verification Checklist

Before building, verify:

- [ ] Lombok plugin is installed and enabled
- [ ] Annotation processing is enabled
- [ ] Java SDK is set to Java 17
- [ ] Maven JRE is set to Java 17
- [ ] Maven project has been imported/reloaded
- [ ] IntelliJ caches have been invalidated (if issues persist)
- [ ] `mvn clean install` works from command line

## 🔍 Diagnostic Commands

Run these to diagnose issues:

```bash
# Check Java version
java -version
javac -version

# Check Maven version
mvn -version

# Maven build with debug info
mvn clean install -X

# Maven build skipping tests
mvn clean install -DskipTests

# Check for Lombok in classpath
mvn dependency:tree | grep lombok
```

## 📝 IntelliJ Build Configuration

For best results, configure IntelliJ to use Maven for building:

1. Go to **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Maven** → **Runner**
2. Check "**Delegate IDE build/run actions to Maven**"
3. This ensures IntelliJ uses Maven's configuration exactly

## 🆘 Still Having Issues?

### Try Building from Command Line
```bash
# Clean build
mvn clean install

# Run application
mvn spring-boot:run
```

If command line works but IntelliJ doesn't:
1. The issue is definitely IntelliJ configuration
2. Try the "Complete Clean Setup Procedure" above
3. Consider updating IntelliJ to the latest version

### Check IntelliJ Idea Version
- **Recommended**: IntelliJ IDEA 2023.2 or later
- Update via **Help** → **Check for Updates**

### Alternative: Use Eclipse or VS Code
If IntelliJ issues persist, the project will work perfectly in:
- Eclipse with Spring Tools
- VS Code with Java Extension Pack
- Command line with `mvn spring-boot:run`

## 💡 Pro Tips

1. **Always build from command line first** to verify project setup
2. **Keep IntelliJ and plugins updated** for best compatibility
3. **Use IntelliJ's Maven integration** rather than manual compilation
4. **Enable "Delegate to Maven"** for consistent builds
5. **Invalidate caches** when strange errors occur

## 🎓 Understanding the Error

The error `java.lang.ExceptionInInitializerError` with `TypeTag::UNKNOWN` occurs when:
- IntelliJ's internal compiler conflicts with Lombok's annotation processor
- Lombok version incompatibility with Java version
- Annotation processing is not properly configured
- Maven compiler plugin needs explicit Lombok configuration

The updated `pom.xml` fixes this by:
- Explicitly specifying Lombok version (1.18.30)
- Configuring maven-compiler-plugin with annotation processor paths
- Setting proper Java source/target versions
- Using `scope=provided` for Lombok dependency

---

**After following these steps, your project should compile successfully in IntelliJ IDEA! 🎉**
