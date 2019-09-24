class idporten_bankid::config inherits idporten_bankid {

  file { "${idporten_bankid::install_dir}${idporten_bankid::application}/${idporten_bankid::artifact_id}.conf":
    ensure  => 'file',
    content => template("${module_name}/${idporten_bankid::artifact_id}.conf.erb"),
    owner   => $idporten_bankid::service_name,
    group   => $idporten_bankid::service_name,
    mode    => '0400',
  } ->
  file { "${idporten_bankid::config_root}${idporten_bankid::application}/application.properties":
    ensure  => 'file',
    content => template("${module_name}/application.properties.erb"),
    owner   => $idporten_bankid::service_name,
    group   => $idporten_bankid::service_name,
    mode    => '0400',
  } ->
  file { "/etc/rc.d/init.d/${idporten_bankid::service_name}":
    ensure => 'link',
    target => "${idporten_bankid::install_dir}${idporten_bankid::application}/${idporten_bankid::artifact_id}.war",
  }
  difilib::logback_config { $idporten_bankid::application:
    application       => $idporten_bankid::application,
    owner             => $idporten_bankid::service_name,
    group             => $idporten_bankid::service_name,
    resilience        => false,
    performance_class => '',
    loglevel_no       => $idporten_bankid::idporten_log_level,
    loglevel_nondifi  => $idporten_bankid::idporten_log_level,
  } ->
  file { "${idporten_bankid::config_root}${idporten_bankid::application}/messages/idporten_en.properties":
    ensure => 'file',
    source => "puppet:///modules/${idporten_bankid::module}/idporten_en.properties",
    owner  => $idporten_bankid::service_name,
    group  => $idporten_bankid::service_name,
  } ->
  file { "${idporten_bankid::config_root}${idporten_bankid::application}/messages/idporten_nn.properties":
    ensure => 'file',
    source => "puppet:///modules/${idporten_bankid::module}/idporten_nn.properties",
    owner  => $idporten_bankid::service_name,
    group  => $idporten_bankid::service_name,
  } ->
  file { "${idporten_bankid::config_root}${idporten_bankid::application}/messages/idporten_se.properties":
    ensure => 'file',
    source => "puppet:///modules/${idporten_bankid::module}/idporten_se.properties",
    owner  => $idporten_bankid::service_name,
    group  => $idporten_bankid::service_name,
  } ->
  file { "${idporten_bankid::config_root}${idporten_bankid::application}/messages/idporten_nb.properties":
    ensure => 'file',
    source => "puppet:///modules/${idporten_bankid::module}/idporten_nb.properties",
    owner  => $idporten_bankid::service_name,
    group  => $idporten_bankid::service_name,
  }

}
