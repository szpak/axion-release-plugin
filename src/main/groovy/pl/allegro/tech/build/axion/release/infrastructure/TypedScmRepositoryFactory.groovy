package pl.allegro.tech.build.axion.release.infrastructure

import pl.allegro.tech.build.axion.release.domain.scm.ScmIdentity
import pl.allegro.tech.build.axion.release.domain.scm.ScmInitializationOptions
import pl.allegro.tech.build.axion.release.domain.scm.ScmRepository
import pl.allegro.tech.build.axion.release.domain.scm.ScmRepositoryFactory
import pl.allegro.tech.build.axion.release.infrastructure.git.GitRepository

class TypedScmRepositoryFactory implements ScmRepositoryFactory {

    private static final String GIT = 'git'

    @Override
    ScmRepository createRepository(String type, File repositoryDir, ScmIdentity identity, ScmInitializationOptions options) {
        ScmRepository repository
        if (type == GIT) {
            repository = new GitRepository(repositoryDir, identity)
        } else {
            throw new IllegalArgumentException("Unsupported repository type $type")
        }

        repository.initialize(options)
        return repository
    }
}
