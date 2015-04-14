# Install the necessary packages: HostAPD, DHCP Server and Webserver packages
sudo apt-get update
sudo apt-get install hostapd isc-dhcp-server apache2 php5 libapache2-mod-php5 -y

# Download Wheelchair GUI and Wheelchair Web UI
cd ~/
git clone GUI-URL
git clone WEB-UI-URL

# Make Web UI available to apache
rm -rf /var/www
ln ~/WEB-UI/ /var/www

# Download and install a version of HostAPD that words with the Edimax Dongle
# Should host this ourselves
wget http://www.daveconroy.com/wp3/wp-content/uploads/2013/07/hostapd.zip
unzip hostapd.zip 
sudo mv /usr/sbin/hostapd /usr/sbin/hostapd.bak
sudo mv hostapd /usr/sbin/hostapd.edimax 
sudo ln -sf /usr/sbin/hostapd.edimax /usr/sbin/hostapd 
sudo chown root.root /usr/sbin/hostapd 
sudo chmod 755 /usr/sbin/hostapd

# Setup HostAPD with a custom config file
mv /etc/hostapd/hostapd.conf /etc/hostapd/hostapd.conf.bak
ln ~/WEB-UI/hostapd.conf /etc/hostapd/hostapd.conf
