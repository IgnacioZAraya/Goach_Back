# General Architecture
## Decoupled:
A Decoupled Architecture is a software development model and concept that fosters independence among the components of 
an application components and allowing them to evolve separately based on the business needs, minimizing 
their dependencies between each other.
### Why choose a Decoupled Architecture?
The main reason for choosing this model was it's two main core ideas: _Autonomy_ & _Self-Determination_, this main concept allows us more 
freedom for the entire development of the app. Here's a more detailed list of advantages that this model brings
through its implementation:
 - Reduces system complexity.
 - Parallel development.
 - Technology flexibility. 
 - Easier CI/CD implementation.
 - Simpler testing.
 - Robust infrastructure against errors.
 - Easier scaling.
 - Easier maintenance.
 - Independent deployment.
 - Component re-usability.
## Architecture Diagrams Links:
- [Context Diagram](https://drive.google.com/file/d/11sUgFmUbcehX0VabdcavpWUAOJyf3D_X/view?usp=sharing)
- [Components Diagram](https://app.diagrams.net/#G1DHyRgoWHNWqTB4jCC8b5J3h9BsZVLW2l#%7B%22pageId%22%3A%22le5_xRo5lFN9-PT3u5oY%22%7D)
- [User Flow Diagram ](https://drive.google.com/file/d/1MK-vLmDyeXdHdQ_njTp2naGWNvo4YZIJ/view?usp=sharing)
# Back-End Architecture:
## Onion Architecture:
An Onion Architecture is a software architectural pattern that forces dependencies flow inward, separating low-level 
modules from high-level ones, but ensure both depend on and follow abstractions, which helps with: 
implementation, maintenance and their independent evolution. Honoring its name, this architecture divides the app 
organization in layers based on abstraction, like so:
- Domain Layer: Handles the business logic with its rules, while also defining the main entities for the app.
- Application Layer: Handles main use cases, app services and acts as the intermediary between the _Domain_ 
_Layer_ and 
the _Infrastructure Layer_.
- Infrastructure Layer: Handles the app interaction with external technologies and dependencies for specific 
use cases.
- Presentation Layer: Handles the main user interaction and the interfaces they will use to do so, usually 
interacting with the Application Layer.
### Why choose an Onion Architecture?
Onion Architecture is rooted in the concept the SOLID principle of dependency inversion, which helps with the 
development in an ecosystem that allows main independence on each layer of the app. This main idea, makes 
every related layer completely flexible, because they depend directly on abstractions, not chaining them to any 
other module or predefined technology, leaving them open for a complete makeover if new business necessities 
demands to.
# Definition of Done:
In any case a new functionality is integrated or any other are fixed in this project, the next criteria must be followed:
- Functionality documented at the code base.
- Local workflow lint test passed.
- Repository workflow unitary tests passed.
- Explicit push request comments with documentation of functional and non-functional requirements' status.
- Approved push request by 1 other team member.
- Integrated to develop branch.
# Linters & Style Conventions:
For all linters set in the Front-End, they were configured in order to lint the code automatically, but still, the command prompts used in order to run them manually are 
going to be found next to each devtool name.
## Expo (ReactNative + TypeScript) & NativewindCSS:
- Prettier.
- ESLint.
### Command for linting:
Due to a chaining implementation between these to linters, you can run both with the next command in your terminal:
- `npm run lint -- [directory/file]`; This command checks the specified directory or file in the app directory, reporting any violation of the rules at the terminal from both linters mentioned above, while fixing those it can, based 
on the ESLint documentation plus the rules in the Prettier configuration. If no file or directory is specified, it'll lint all files in the _.app_ directory.
- Links for linting rules documentation: [ESLint](https://eslint.org/docs/latest/rules) & [Prettier](https://prettier.io/docs/configuration)
## Spring Boot:
- Sonarlint.
### Command for linting:
- `./gradlew sonar
  -Dsonar.projectKey=GoachBackEnd
  -Dsonar.projectName='GoachBackEnd'
  -Dsonar.host.url=http://localhost:9000/
  -Dsonar.token=[YOUR_TOKEN]`; This command checks for static analysis of the whole BackEnd code locally, for more information consult these videos: [Install Video](https://www.youtube.com/watch?v=7UIWPIWrkpY) & [Setup Video](https://youtu.be/v3evzZ-JOQo?si=yppzB-CNykROntnA).
# CI/CD Pipelines
There are two ways to execute the workflows to check for linting and for testing the code: via pushing to the repository or via local execution with **Docker** & **Nektos/Act**, the first 
option, triggers automatically when any push happens in the repository, meanwhile the second option must be handled manually by command prompts and docker, to 
accomplish it run any of the next command prompts:
- `act`; it will run all workflows setups in the **.github/workflows** directory.
- `act [GITHUB_EVENT]`; it will run all jobs related to one specific github event.
- `act -j [JOB_NAME]`; it will run an specific job.
- `act -j [JOB_NAME] -W [WORKFLOW_PATH]`; it will run a job in a specific workflow.
# Development Standards:
## Branch strategy (Git Flow):
### main:
- Only tested code in production.
- Versioned.
- Protected: No direct commits.
### develop:
- CI for the new releases.
- Protected: Mandatory PRs.
### feature/:
- Base: develop.
- Convention: feature/<task>-<short-slug>.
- Short Life; merge to develop vía PR.
### release/:
- Base: develop.
- Freezes scope, only fixes, docs & chores.
### Minimum Protection Standards:
- PRs require 1 approval.
- Status checks: build, tests, lint, coverage.
- Fast-forward disabled; standard repo rebase or merge commit.
## Commit Conventions
- type: review
## Name Conventions
### Code (General):
- Classes/Types (Java/TS): PascalCase.
- Interfaces (TS): PascalCase without prefixes.
- Variables/functions (JS/TS/Java): camelCase.
- Constants: UPPER_SNAKE_CASE.
- Java Packages: cr.package.<domain>.<moduleo> in lower.snake if applies.
- Frontend Directories: kebab-case (lowercase with dashes).
- Tests: same names as the test subject + test suffix.
### APIs REST
- Base & Version: /api/v1.
- Resources: Plural nouns, routes with kebab-case.
- Sub-resources: Hierarchical when there's a strong relation.
- No CRUD Actions: /actions with an explicit verb.
- IDs: UUID or numeric, never semantics.
- Pagination: page, size, sort.
- Filters: query params with consistent names. 
- HTTP Codes: standard; errors within consistent payloads.
- JSON: camelCase ks.
### Frontend (React Native + Nativewind)
- Components: PascalCase; 1 file per component.
- Hooks: useXxx in camelCase.
- Contexts/Providers: XxxProvider, XxxContext.
- Routes (router): kebab-case; lazy wherever it applies.
- Assets: kebab-case; static in public/ o assets/.
- Utility Styles: Nativewind; miss CSS except in @layer.
## PR Checklist
### General
- Clear títles and descriptions; link to task/incident.
- Branch aligned Scope (feature/, release/).
### Quality
- Green Build; linters and format applied.
- Passed Tests; coverage must not decrease.
- Documented new routes & DTOs.
### Security & Data
- No secrets in códe/config.
- Entrance validation; errors must be handled with consistency.
- Role & permissions check if endpoints are met.
### Performance & UX
- No heavy queries without index.
- Lazy loads where they matter (frontend).
- Basic accessibility: labels, focus, alt, semantics
### Operation
- Environment variables must be documented.
- Feature flags must be declared & by default deactivated in main.
- Updated CI/CD scripts.
### Checks
- At least 1 approval.
- Resolved comments.
- Resolved conflict with clean rebase.