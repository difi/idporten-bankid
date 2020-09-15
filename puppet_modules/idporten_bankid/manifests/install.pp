class idporten_bankid::install inherits idporten_bankid {

  user { $idporten_bankid::service_name:
    ensure => present,
    shell  => '/sbin/nologin',
    home   => '/',
  } ->
  file { $idporten_bankid::idporten_install_dir:
    ensure  => 'directory',
    replace => false,
    owner   => $idporten_bankid::service_name,
    group   => $idporten_bankid::service_name,
  }
  file { "${idporten_bankid::config_root}${idporten_bankid::application}":
    ensure => 'directory',
    owner  => $idporten_bankid::service_name,
    group  => $idporten_bankid::service_name,
    mode   => '0755',
  } ->
  file { "${idporten_bankid::log_root}${idporten_bankid::application}":
    ensure => 'directory',
    owner  => $idporten_bankid::service_name,
    group  => $idporten_bankid::service_name,
    mode   => '0755',
  }
  file { "${idporten_bankid::config_root}${idporten_bankid::application}/messages":
    ensure => 'directory',
    owner  => $idporten_bankid::service_name,
    group  => $idporten_bankid::service_name,
    mode   => '0755',
  }
  file { "${idporten_bankid::tomcat_tmp_dir}":
    ensure => 'directory',
    mode   => '0775',
    owner  => $idporten_bankid::service_name,
    group  => $idporten_bankid::service_name,
  }
}