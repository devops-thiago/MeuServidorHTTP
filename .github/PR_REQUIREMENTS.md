# Pull Request and Merge Requirements

This repository has automated checks that must pass before pull requests can be merged to the main branch.

## Required Checks

### 1. Tests Must Pass ✅
- All unit tests must execute successfully
- Tests are run using Maven: `mvn test`
- Test failures will block the PR from being merged

### 2. Code Coverage ≥ 80% 📊
- JaCoCo measures code coverage during test execution
- Instruction coverage must be at least 80%
- Coverage is checked using: `mvn jacoco:check`
- Coverage reports are uploaded to [Codecov](https://codecov.io/gh/devops-thiago/MeuServidorHTTP)

### 3. SonarCloud Quality Gate ✅
- Code quality analysis is performed by SonarCloud
- Quality gate must pass (no critical/major issues)
- Analysis is run using: `mvn sonar:sonar`
- Results available at [SonarCloud](https://sonarcloud.io/project/overview?id=devops-thiago_MeuServidorHTTP)

## Automated Workflows

### PR Checks (`pr-checks.yml`)
Triggered on every pull request to `master`/`main`:
- Compiles code
- Runs tests
- Generates coverage report
- Validates 80% coverage threshold
- Uploads coverage to Codecov
- Runs SonarCloud analysis

### Merge Validation (`merge-checks.yml`)
Triggered when code is merged to `master`/`main`:
- Same checks as PR validation
- Ensures main branch always meets quality standards

### CI Workflow (`ci.yml`)
General CI pipeline that runs on pushes and PRs:
- Comprehensive validation
- Caches Maven dependencies for faster builds
- Reports results to external services

## Branch Protection

The main branch should be configured with the following protection rules:
- Require pull request reviews
- Require status checks to pass before merging:
  - `PR Validation - Tests, Coverage & Quality Gate`
- Require branches to be up to date before merging
- Restrict pushes that bypass pull requests

## Local Development

Before creating a PR, ensure your changes pass all checks:

```bash
# Run tests
mvn clean test

# Check coverage
mvn jacoco:report jacoco:check

# Run SonarCloud analysis (requires SONAR_TOKEN)
mvn sonar:sonar
```

## Secrets Configuration

The following secrets must be configured in the repository:
- `CODECOV_TOKEN`: Token for uploading coverage to Codecov
- `SONAR_TOKEN`: Token for SonarCloud analysis
- `GITHUB_TOKEN`: Automatically provided by GitHub Actions

## Coverage Improvement

If coverage falls below 80%, consider:
1. Adding unit tests for uncovered code paths
2. Removing unnecessary/unreachable code
3. Testing edge cases and error conditions
4. Mocking external dependencies in tests