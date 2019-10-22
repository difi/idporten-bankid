#init.pp
class idporten_bankid (
  $bankid_webaddress_hostname                    =$idporten_bankid::params::bankid_webaddress_hostname,
  $bankid_webaddress_ip                          =$idporten_bankid::params::bankid_webaddress_ip,
  $node_environment                              =$idporten_bankid::params::node_environment,
  $bankid_enabled                                =$idporten_bankid::params::bankid_enabled,
  $bankid_action                                 =$idporten_bankid::params::bankid_action,
  $bankid_clienttype                             =$idporten_bankid::params::bankid_clienttype,
  $bankid_keystore_file                          =$idporten_bankid::params::bankid_keystore_file,
  $bankid_keystore_password                      =$idporten_bankid::params::bankid_keystore_password,
  $bankid_merchant_name                          =$idporten_bankid::params::bankid_merchant_name,
  $bankid_grantedpolicies                        =$idporten_bankid::params::bankid_grantedpolicies,
  $bankid_logging_enabled                        =$idporten_bankid::params::bankid_logging_enabled,
  $bankid_logging_prop_file                      =$idporten_bankid::params::bankid_logging_prop_file,
  $bankid_logging_category_name                  =$idporten_bankid::params::bankid_logging_category_name,
  $bankid_ocsp_max_time_skew                     =$idporten_bankid::params::bankid_ocsp_max_time_skew,
  $bankid_cors_allow_origin                      =$idporten_bankid::params::bankid_cors_allow_origin,
  $bankid_suppress_broadcast                     =$idporten_bankid::params::bankid_suppress_broadcast,
  $bankid_basic_username                         =$idporten_bankid::params::bankid_basic_username,
  $bankid_basic_password                         =$idporten_bankid::params::bankid_basic_password,
  $bankidmobil_enabled                           =$idporten_bankid::params::bankidmobil_enabled,
  $config_root                                   =$idporten_bankid::params::config_root,
  $log_root                                      =$idporten_bankid::params::log_root,
  $module                                        =$idporten_bankid::params::module,
  $application                                   =$idporten_bankid::params::application,
  $context                                       =$idporten_bankid::params::context,
  $install_dir                                   =$idporten_bankid::params::install_dir,
  $server_port                                   =$idporten_bankid::params::server_port,
  $idporten_redirect_url                         =$idporten_bankid::params::idporten_redirect_url,
  $java_home                                     =$idporten_bankid::params::java_home,
) inherits idporten_bankid::params {

  include platform


  anchor { 'idporten_bankid::begin': } ->
  class { '::idporten_bankid::install': } ->
  class { '::idporten_bankid::deploy': } ->
  class { '::idporten_bankid::config': } ~>
  class { '::idporten_bankid::test_setup': } ->
  class { '::idporten_bankid::service': } ->
  anchor { 'idporten_bankid::end': }
}
